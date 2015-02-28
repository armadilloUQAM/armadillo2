
package biologic.seqclasses;

import com.ibm.wsdl.PortTypeImpl;
import configuration.Config;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPPart;
import org.apache.axis.Message;
//import org.apache.axis.SOAPPart;
import org.apache.axis.client.Call;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.gen.Parser;
import org.apache.axis.wsdl.WSDL2Java;



/**
 *
 * @author Etienne Lord
 * @since June 2010
 */
public class usewsdl {
    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    Call call=null;
    Vector<InputParams>inputparams=new Vector<InputParams>();

    private Parser wsdlparser=null;


    ////////////////////////////////////////////////////////////////////////////
    /// Constructor
    public usewsdl() {}

    ////////////////////////////////////////////////////////////////////////////
    /// Function
    public boolean loadWSDL(String wsdl_url) {
        wsdlparser=new Parser();
        try {
        wsdlparser.run(wsdl_url);
        } catch(Exception e) {
            e.printStackTrace();
            Config.log(e.getMessage()+"\n"+e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    
    public void callwstojava(String wsdl_url, String filename) {
       //--This will generate a stub that we can use to call web serveice...
        String[] params = new String[] {"-v", "-D", "-o"+filename, wsdl_url};    
        WSDL2Java.main(params);
    }
    
    public String return_WSDL_endpointURI() {
        if (this.wsdlparser==null) return "";
        return this.wsdlparser.getCurrentDefinition().getDocumentBaseURI();
    }

    public String return_WSDL_targetNamespace() {
        if (this.wsdlparser==null) return "";
        //System.out.println(this.wsdlparser.getCurrentDefinition().getTargetNamespace());
        return this.wsdlparser.getCurrentDefinition().getTargetNamespace();
    }

    public Vector<Service> return_WSDL_Service() {
        Vector<Service> list=new Vector<Service>();
        if (this.wsdlparser==null) return list;
        Map<Object,Service> m=this.wsdlparser.getCurrentDefinition().getServices();
        for (Object key:m.keySet()) {
            list.add(m.get(key));
        }
        return list;
    }

    public Vector<Operation> return_WSDL_Operation() {
        Vector<Operation> list=new Vector<Operation>();
        if (this.wsdlparser==null) return list;
        Map<Object,Binding> m=this.wsdlparser.getCurrentDefinition().getBindings();
        for (Object key:m.keySet()) {
            Binding binding=m.get(key);
            List<Operation> l=binding.getPortType().getOperations();
                    for (Operation operation:l) {
                        list.add(operation);
                    }
        }
        return list;
    }

    public Vector<com.ibm.wsdl.PortTypeImpl> return_WSDL_Port() {
        Vector<PortTypeImpl> list=new Vector<PortTypeImpl>();
        if (this.wsdlparser==null) return list;
        Map<Object,com.ibm.wsdl.PortTypeImpl> m=this.wsdlparser.getCurrentDefinition().getPortTypes();
        for (Object key:m.keySet()) {
            com.ibm.wsdl.PortTypeImpl port=m.get(key);
            list.add(port);
        }
        return list;
    }
    public Vector<Part> return_WSDL_InputPart(Input input) {
        Vector<Part> list=new Vector<Part>();
        if (input==null) return list;
        Map<Object,Part> m=input.getMessage().getParts();
        for (Object key:m.keySet()) {
            list.add(m.get(key));
        }
        return list;
    }

    public Vector<Part> return_WSDL_OutputPart(Output output) {
        Vector<Part> list=new Vector<Part>();
        if (output==null) return list;
        Map<Object,Part> m=output.getMessage().getParts();
        for (Object key:m.keySet()) {
            list.add(m.get(key));
        }
        return list;
    }

    public boolean createWSDL(int OperationNumber)  {
        //--Variables
        org.apache.axis.client.Service service=new org.apache.axis.client.Service();
        Operation operation=this.return_WSDL_Operation().get(OperationNumber);
        Vector<Part> input=this.return_WSDL_InputPart(operation.getInput());
        Vector<Part> output=this.return_WSDL_OutputPart(operation.getOutput());
        this.inputparams.clear();
        try {
            System.out.println("\nWSDL operation: "+operation.getName()+"\nWebService: "+this.return_WSDL_targetNamespace());
            call=(Call) service.createCall();
            call.setTargetEndpointAddress(this.return_WSDL_targetNamespace());
            System.out.println("Endpoint Adress: "+this.return_WSDL_targetNamespace());
            call.setOperationName(new QName(this.return_WSDL_targetNamespace(),operation.getName()));
            //call.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, "");
            //call.setProperty(Call.OPERATION_STYLE_PROPERTY, "wrapped");
            call.setOperationStyle(Style.WRAPPED);
            call.setOperationUse(Use.LITERAL);
            
            System.out.println("Input:");
            for (Part i:input) {
                 System.out.println(i.getName());
                 
                 call.addParameter(i.getName(), i.getTypeName(), ParameterMode.IN);
                 //InputParams in=new InputParams(i.getName(), i.getTypeName().getLocalPart());
                 //this.inputparams.add(in);
            }
            System.out.println("List input:");
            for (InputParams i:this.inputparams) System.out.println(i);
            for (Part o:output) {
                System.out.println(o);
                call.addParameter(o.getName(), o.getTypeName(), ParameterMode.OUT);                
            }
            call.setReturnType(XMLType.XSD_ANY);
            Object[] params=new Object[input.size()];
            
            for (int i=0;i<params.length;i++) params[i]=new String();
            //
//            call.invoke(params);
//            Message m=call.getResponseMessage();
//            Iterator<MimeHeader> iter=m.getSOAPPart().getAllMimeHeaders();
//            SOAPPart soap=m.getSOAPPart();
            //--Info
//            while(iter.hasNext()) {
//                MimeHeader mime=iter.next();
//                System.out.println(mime.getName()+":"+mime.getValue());
//            }                        
//            System.out.println(m.getSOAPPartAsString());
            

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("\nError running :"+operation.getName()+"\nWebService: "+this.return_WSDL_targetNamespace()+"\n"+ex.getMessage());
            Config.log("\nError running :"+operation.getName()+"\nWebService: "+this.return_WSDL_targetNamespace()+"\n"+ex.getMessage());
        }
        return true;
    }

    public void printInfo() {
        System.out.println("endpoint: "+this.return_WSDL_endpointURI());
        System.out.println("namespace: "+this.return_WSDL_targetNamespace());
        System.out.println("services:");
        for (Service s:this.return_WSDL_Service()) {
            System.out.println("\t"+s.getQName().getNamespaceURI());
            System.out.println("\t"+s.getQName().getLocalPart());
        }
        System.out.println("port:");
        for (com.ibm.wsdl.PortTypeImpl s:this.return_WSDL_Port()) {
            System.out.println("\t"+s.getQName());
        }
        System.out.println("Operations:");
        int operation_count=0;
        for (Operation s:this.return_WSDL_Operation()) {
            System.out.println("["+operation_count+"] "+s.getName());
            System.out.println("\tInput\t"+s.getInput().getName());
            //--List part
            try {
            for (Part p:this.return_WSDL_InputPart(s.getInput())) {
                System.out.println("\t\t"+p.getName()+" "+p.getTypeName().getLocalPart());
            }
            System.out.println("\tOutput\t"+s.getOutput().getName());
            } catch(Exception e){}
            try {
            for (Part p:this.return_WSDL_OutputPart(s.getOutput())) {
                System.out.println("\t\t"+p.getName()+" "+p.getTypeName().getLocalPart());
            }
            } catch(Exception e) {}
            operation_count++;
        }
        }

    class InputParams {
        public String type="";
        public String name="";
        public Object data=null;
        public InputParams(String type, String name) {
            this.type=type;
            this.name=name;
        }
        public void setData(Object data) {
            this.data=data;
        }
        public Object getData() {
            return data;
        }

        @Override
        public String toString() {
            return this.name+":"+this.type+":"+this.data;
        }


    }

}

