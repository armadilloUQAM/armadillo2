#!/usr/bin/python

"""
This script is an interface between Armadillo and Mammouth-Mp2
It could also be used for other Calcul Québec Clusters
It use basic values for the bash file created

It has been made has a proof of concept that a workflow could be run
localy and on a cluster with Armadillo

Date   : Last update 1 mars 2016
Author : J.G.

# TO DO
# REMOVE FILES AND DIR ON SERVER WHEN DOWNLOADED
    -> easy : use h["ClusterPgrmName"]+"_"+h["Order"]
    -> medium : linked this to user choice in Armadillo
                see ClusterEditors to add informations
# Test for all modules available not only bioinformatics
    -> easy : Change SP_Extractor
    -> medium : linked this to cluster choice in Amadillo to have the
                extact command line needed to have modul available
# Improve bash creation
    -> medium : need to be setted also in armadillo
    -> easy :   change the way to extract info from stdout by using key
                words
    -> medium : need to be correlated with cluster choosed in Armadillo
                Had Cluster.properties ? and Load it
# Improve time waiting
    -> medium : Correlated with information contained in bash file, user
                need
# Add the option to come back later and not relaunch the bash file
    -> medium : saved the process ID from bash submission add it to the
                workflow
# Add all potentials error status
    -> hard :   Check in results from cluster
                Import it in Armadillo
"""

import os
import sys
import re
import subprocess
import time

def Main():
    (tt,cObj,clus) = arg_Values()
    (cObj)         = restore_Obj(cObj)
    (hcf)          = hash_Info(cObj)
    pwd            = get_cluster_pwd(clus);
    
    if (tt==0):
        (lP,nP) = SP_Extractor(clus)
        (tb,p)  = Pgrm_Validator(hcf,lP)
        if (not tb):
            print "NotAbleToDoItOnCluster"
        else:
            print "<>ClusterPgrmName<>"+p+"<>ClusterPWD<>"+pwd+"<>"
    elif (tt>0) :
        kb  = len(hcf)
        hcf = prepare_Files(hcf)
        hcf = prepare_BashFile(hcf,tt)
        if (tt==1):
            ka  = len(hcf)
            if kb < ka:
                print "Files Prepared"
        elif (tt==2):
            send_Files(hcf,clus)
            print "Files Sended"
        elif (tt==3):
            print "step execute bash"
            execute_Bash_on_cluster(hcf,clus)
            print "bash executed"
        elif (tt==4):
            print "get results"
            download_results(hcf,clus)
            print "results downloaded"
        elif (tt==5):
            print "Import Data in Armadillo"
            (stdout,stderror) = import_results(hcf)
            print stdout+stderror
    else:
        print "can't found a good argument"

#
#
# Short and sweet functions
#
#

# Create regex for good lines
def good_Lines():
    goodLine  = ["^Name","^Version","^Commandline_Running","^input_[a-z]_id\d+",'^FileNameFromId','^Order','^output_([a-z]+)_fileName','^input_\w+_id\d+_properties',"^Executable.*","^ClusterPgrmName.*","^ClusterPWD.*"]
    gL = "(" + ")|(".join(goodLine) + ")"
    return gL

# Return a match list of executable
def get_list_exe(h):
    l = []
    for k in h.keys():
        if re.search("^Executable.*",k,re.IGNORECASE):
            l.append(h[k])
    ml = "(" + ")|(".join(l) + ")"
    return l

# Find file in string
def find_file(s):
    m   = s.rfind('/')
    if m == -1:
        m   = s.rfind('\\')
    if m >= 0:
        s = s[m+1:len(s)]
    return s

# Return true if the file has an extention false if not
def file_has_ext(s):
    s   = find_file(s)
    m   = s.rfind('.')
    if m == -1:
        return False
    else:
        return True

# Return true if the file has an extention false if not
def path_is_dir(s):
    m   = s.rfind('/')
    if m == -1:
        m   = s.rfind('\\')
    if m == len(s):
        return True
    else:
        return False

# Get the directory of files
def get_path_dir_of_file(s):
    m   = s.rfind('/')
    if m == len(s):
        s = s[0:m-1]
        m = s.rfind('/')
    if m == -1:
        m   = s.rfind('\\')
    if m >= 0:
        s = s[0:m]
    return s

# Create and send ssh command
def file_already_on_cluster(clus,c,r):
    (outs) = ssh_command(clus,c)
    if re.search(r,outs[0],re.IGNORECASE):
        return True
    else:
        return False

# Create and send ssh command
def get_cluster_pwd(clus):
    (outs) = ssh_command(clus,'pwd')
    if outs[0]!="":
        return outs[0]
    else:
        return "NotAbleToDoItOnCluster"

# Create and send ssh command
def ssh_command(clus,c):
    commands=['ssh', clus, c]
    # Command and extractor
    ssh = subprocess.Popen(commands,
                            shell=False,
                            stdout=subprocess.PIPE,
                            stderr=subprocess.PIPE)
    (outs) = ssh.communicate()
    return outs

# Generic SCP COMMAND LINE
# Source http://stackoverflow.com/questions/68335/how-do-i-copy-a-file-to-a-remote-server-in-python-using-scp-or-ssh
# Comment by Charles Duffy
# outs = subprocess.Popen(["scp", filename, "%(user)s@%(server)s:%(remotepath)s" % vars]).wait() 
# Send Files from local to server
def scp_command_send(f, c):
    outs = ""
    opti = ""
    if not file_has_ext(f):
        f = ""+get_path_dir_of_file(f)
        opti = "-r"
    if not file_has_ext(c):
        c = ""+get_path_dir_of_file(c)
    if opti!="":
        outs = subprocess.Popen(["scp", opti, f, c]).wait() 
    else:
        outs = subprocess.Popen(["scp", f, c]).wait() 
    return outs
# Get Files from server to local
def scp_command_des(f, c):
    outs = ""
    opti = "-r"
    outs = subprocess.Popen(["scp", opti, f, c]).wait() 
    return outs

#
#
# Program functions
#
#

# Extract file argument if exists
def arg_Values():
    testType = -1
    cObj     = ""
    clus     = ""
    allArgs  = " ".join(sys.argv[1:]);
    m0 = re.compile(r'\-obj (.*) \-clus (.*)$', re.IGNORECASE)
    
    for ar in range(1,len(sys.argv)):
        if sys.argv[ar]=="-test":
            testType = 0
        if sys.argv[ar]=="-prepare":
            testType = 1
        if sys.argv[ar]=="-send":
            testType = 2
        if sys.argv[ar]=="-launch":
            testType = 3
        if sys.argv[ar]=="-waitResults":
            testType = 4
        if sys.argv[ar]=="-importResults ":
            testType = 5
    mm0 = m0.search(allArgs);
    
    if mm0:
        cObj = mm0.group(1)
        clus = mm0.group(2)
        
    return (testType,cObj,clus)

# Restore cObj
def restore_Obj(cObj):
    val = {r'<_____>':r'<>',r'<__->__>':r'->',r'<__n__>':r'\n',r'_____':r' ',r'  ':r' '}
    tab = [r'<_____>',r'<__->__>',r'<__n__>',r'_____',r'  ']
    
    for i in range(0,len(tab)):
        m = re.compile(tab[i], re.IGNORECASE)
        if m.search(cObj):
            while m.search(cObj):
                cObj = re.sub(tab[i],val[tab[i]],cObj,re.IGNORECASE)
    return (cObj)
    
# Create an hash table with : program keys -> program values
def hash_Info(cObj):
    hashcObj = {}
    gL = good_Lines();
    m1 = re.compile("^(\w*)->(.*)$", re.IGNORECASE)
    m2 = re.compile(gL, re.IGNORECASE)
    m3 = re.compile('^input_\w+_id\d+_properties', re.IGNORECASE)
    m4 = re.compile('FileNameFromId\-\>((\.?\/\w+)+(\.\w+)?)', re.IGNORECASE)
    tab = cObj.split('\n');
    for t in tab:
        mm1 = m1.search(t)
        if mm1:
            k   = mm1.group(1)
            mm2 = m2.search(k)
            mm3 = m3.search(k)
            if mm2:
                v = mm1.group(2)
                if mm3:
                    mm4 = m4.search(v)
                    if mm4:
                        v = mm4.group(1)
                hashcObj[k]=v
    return hashcObj

# Extract programs and version from module available online
# http://python-for-system-administrators.readthedocs.org/en/latest/ssh.html
def SP_Extractor(clus):
    listProg = {}
    numPgrm  = 0
    count    = 0
    ress     = []
    s        = re.compile('^bioinformatics/(\w+)/(\w+(\.?\w+)*)', re.IGNORECASE)
    (outs)   = ssh_command(clus,'module avail')
    for out in outs:
        if out == "":
            count+=1
    if count == 1:
        for out in outs:
            if (out != ""):
                res = out
        ress = res.split('\n');
    # values extractor
    if ress != []:
        for result in ress:
            s_ = s.search(result)
            if s_:
                name    = s_.group(1)
                version = s_.group(2)
                listProg[name] = version
                numPgrm+=1
    #print "The number of bio-informatics program in ",clus," is ",numPgrm,"\n"
    return (listProg,numPgrm)

# Valid if the workflow use programs present on the server
def Pgrm_Validator(h,listProg):
    testbool = False
    for p in listProg:
        v = "^"+p+"$"
        m = re.search(v,h["Name"], re.IGNORECASE)
        if m:
            h["Name"] = p
            testbool = True
    return (testbool,h["Name"])

# Prepare an hash table for files sources destination and directory
# Prepare an hash table for files sources destination and directory
def prepare_Files(h):
    c      = 0
    pgrmID = h["ClusterPgrmName"]+"_"+h["Order"]
    d      = h["ClusterPWD"]+"/"+pgrmID+"/"
    
    h["pgrmID"] = pgrmID
    h["Clus_dir"] = d
    h["Clus_sou"] = "./tmp/cluster/"+pgrmID+"/"
    
    if not os.path.exists(h["Clus_sou"]):
        os.makedirs(h["Clus_sou"])
    
    for i in h.keys():
        if re.match(r'^input',i,re.IGNORECASE):
            des = h[i]
            des = find_file(des)
            h[`c`+"_des_name"] = des
            if not file_has_ext(des):
                des = des+"/"+des
            des = d+des
            h[h[i]] = des
            h[`c`+"_des"] = des
            h[`c`+"_sou"] = h[i]
            c=c+1
    out_sou = get_output_File(h)
    out_des = out_sou
    out_des = find_file(out_des)
    h["OF_des_name"] = out_des
    if not file_has_ext(out_des):
        out_des = out_des+"/"+out_des
    out_des = d+out_des
    h[out_sou] = out_des
    h["OF_sou"] = out_sou
    h["OF_des"] = out_des
    
    ccl = get_cluster_commandline(h)
    #print ccl
    h["Cluster_Commandline_Running"] = ccl
    return h

# Return the cluster command line
def get_cluster_commandline(h):
    s = h["Commandline_Running"]
    l = s.split(' ')
    ccl = ""
    
    # In windows os the command would start by something like exec ...
    # Let's found the real command line
    # Should be improved if it's a docker file
    li = get_list_exe(h)
    z  = 0
    for q in range(0,len(l)):
        for i in range(0,len(li)):
            if re.search(l[q],li[i],re.IGNORECASE):
                z = q
                break
        if z == q:
            break
    l[z] = find_file(l[z])
    for i in range(z+1,len(l)):
        b = 0
        if l[i]!="":
            for cm in h.keys():
                if re.search(cm,l[i],re.IGNORECASE):
                    l[i] = cm
                    b = 1
        if b==1:
            ccl = ccl +" "+ h[l[i]]
        else:
            ccl = ccl +" "+ l[i]
    ccl = l[z]+" "+ccl
    return ccl

# Return the output path by removing all options and known path
def get_output_File(h):
    s   = h["Commandline_Running"]
    l   = s.split(' ')
    ccl = ""
    b   = 1
    li = get_list_exe(h)
    ml = "(" + ")|(".join(li) + ")"
    
    for i in range(0,len(l)):
        b = 1
        if re.search(r'^\-\w+',l[i],re.IGNORECASE):
            b = 0
        elif re.search(l[i],ml,re.IGNORECASE):
            b = 0
        elif l[i]!="":
            for cm in h.keys():
                if re.search(cm,l[i],re.IGNORECASE):
                    l[i] = cm
                    b = 0
        if b==1:
            ccl = ccl+l[i]
    return ccl

# Prepare a bash file
def prepare_BashFile(h,tt):
    bsn   = ""+h["ClusterPgrmName"]+"_"+h["Order"]+"_bash.sh"
    bs    = ""+h["Clus_sou"]+""+bsn
    bd    = h["Clus_dir"]+bsn
    stdoc = h["Clus_dir"]+""+"stdOutFile"
    stdec = h["Clus_dir"]+""+"stdErrFile"
    stdol = ""+h["Clus_sou"]+""+"stdOutFile"
    stdel = ""+h["Clus_sou"]+""+"stdErrFile"
    if tt==1:
        of = open(bs, 'w');
        of.write(
            "#!/bin/bash\n"
            "#PBS -l walltime=00:05:00\n"
            "#PBS -l nodes=1:ppn=1\n"
            "#PBS -q qwork@mp2\n" # TO MODIFY DEPENDING ON SERVER
            #"#PBS -r n\n"
            "#PBS -o "+stdoc+"\n"
            "#PBS -e "+stdec+"\n"
            #"#PBS -M email@email.com\n"
            "module load bioinformatics/"+h["ClusterPgrmName"]+"/"+h["Version"]+"\n"
        )
        #"cd ~/"+hashcObj["Clus_dir"]+"\n"
        if not file_has_ext(h["OF_des"]):
            mdir = get_path_dir_of_file(h["OF_des"])
            of.write("mkdir "+mdir+"\n")
        of.write(""+h["Cluster_Commandline_Running"]+"\n")
        of.close
    
    h["Bash_File_sou"] = bs
    h["Bash_File_des"] = bd
    h["Bash_File_des_file"] = bsn
    h["stdOutC_File"]  = stdoc
    h["stdErrC_File"]  = stdec
    h["stdOutL_File"]  = stdol
    h["stdErrL_File"]  = stdel
    return h

# Create directory and send files
def send_Files(h,clus):
    c = "mkdir "+h["Clus_dir"]
    (outs) = ssh_command(clus,c)
    
    for k in h.keys():
        if re.match(r'^\d+_des$',k):
            m=re.search(r'^(\d+)_des',k)
            v = m.group(1)
            c = " find "+h["Clus_dir"]+" -name "+h[v+"_des_name"]+""
            r = ""+h["Clus_dir"]+""+h[v+"_des_name"]+""
            bOnC = file_already_on_cluster(clus,c,r)
            if not bOnC:
                f = ""+h[v+"_sou"]+""
                c = ""+clus+":"+h[k]+""
                outs = scp_command_send(f,c)
    
    c = " find "+h["Clus_dir"]+" -name "+h["Bash_File_des_file"]+""
    r = h["Bash_File_des"]
    bOnC = file_already_on_cluster(clus,c,r)
    if not bOnC:
        f = ""+h["Bash_File_sou"]+""
        c = ""+clus+":"+h["Bash_File_des"]+""
        outs = scp_command_send(f,c)
    
# Execute the bash file on server
def execute_Bash_on_cluster(h,clus):
    c = " find "+h["Clus_dir"]+" -name "+h["Bash_File_des_file"]+""
    r = ""+h["Clus_dir"]+""+h["Bash_File_des_file"]+""
    bOnC = file_already_on_cluster(clus,c,r)
    if bOnC:
        c = "cd "+h["Clus_dir"]+" && qsub "+h["Bash_File_des"]
        (outs) = ssh_command(clus,c)
    else:
        print "Bash File is not on server"
  
# Dowload Files and results when it's done
def download_results(h,clus):
    #l = [60,60,60,60,60,60,120,240,480,960,1920]
    l = [60,60,60,60,60,60]
    i = 0
    t = 0
    b = True
    c = " find "+h["Clus_dir"]+" -name "+h["OF_des_name"]+""
    r = ""+h["Clus_dir"]+""+h["OF_des_name"]+""
    while b and i<len(l):
        bOnC = file_already_on_cluster(clus,c,r)
        if bOnC:
            b = False
        else:
            t+=l[i]
            time.sleep(l[i])
            i+=1
    if b==False:
        f = clus+":"+h["Clus_dir"]
        c = h["Clus_sou"]
        out = scp_command_des(f,c)
        print "<>DONE<>"
        if not file_has_ext(h["OF_des"]):
            f = clus+":"+get_path_dir_of_file(h["OF_des"])+"/"
        else :
            f = clus+":"+h["OF_des"]
        if not file_has_ext(h["OF_sou"]):
            c = get_path_dir_of_file(get_path_dir_of_file(h["OF_sou"]))+"/"
        else :
            c = h["OF_sou"]
        out = scp_command_des(f,c)
        print "Files"
    else :
        print "Results Files not found in "+`t`+" seconds, Please test it later. Server is busy"

# Import results in Armadillo    
def import_results(h):
    f = {}
    for root, dirs, files in os.walk(h["Clus_sou"]):
        for file in files:
            if file.endswith("stdErrFile"):
                f["stdErrFile"]=os.path.join(root, file)
            if file.endswith("stdOutFile"):
                f["stdOutFile"]=os.path.join(root, file)
    stdout = "<>STDOUT<>"
    b1 = False
    b2 = False
    with open(f["stdOutFile"], 'r') as fi:
        for line in fi:
            if re.match(r'^END.+',line,re.IGNORECASE):
                print line
                b1 = True
            if re.match("^-+$",line) and b1:
                b2 = True
            if re.match("^-+$",line) and b2 and stdout!="<>STDOUT<>":
                b2 = False
            if b2 and not re.match("^-+$",line):
                stdout = stdout + line
    stdout = restruct_Obj(stdout)
    stderror = "<>STDERROR<>"
    with open(f["stdErrFile"], 'r') as content_file:
        stderror = stderror+content_file.read()
    stderror = restruct_Obj(stderror)
    return (stdout,stderror)

# Restructure a string to be transfert between script and Armadillo
def restruct_Obj(s):
    val = {r'<>':r'<_____>',r'->':r'<__->__>',r'\n':r'<__n__>',r' ':r'_____'}
    tab = [r'<>',r'->',r'\n',r' ']
    
    for i in range(0,len(tab)):
        m = re.compile(tab[i], re.IGNORECASE)
        if m.search(s):
            while m.search(s):
                s = re.sub(tab[i],val[tab[i]],s,re.IGNORECASE)
    return s

Main()
