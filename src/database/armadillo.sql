-- ---^^^^^^^^^---
--  ---====================================---
-- --- 	Armadillo tables creation		---
-- ---			Created 2009-06-08	---
-- ---			Copyright 2009		---
-- ---		Leclercq Mickael, Lord Etienne,	---
-- --- Baniré Abdoulaye Diallo, Boc Alix	---
--  ---====================================---
-- ---___***___---

-- Updated: 15 June 2009
-- Updated: 7 October 2009 - Etienne
-- Updated: 4 January 2010 - Etienne
-- Updated: 17 February 2010 - Etienne
-- Updated: July 2012 - Etienne
-- Updated: Septembre 2014 - Etienne - Added table T3_columns

--
-- CREATE TABLE: T3_Column (Generic three columns datatypes)
--
--  i: ID
--  p: properties
--  v: value
--  t: timestamps
--
CREATE TABLE T3_Column
(
	i TEXT,
	p TEXT,
	v TEXT,
	t TEXT
);


--
-- CREATE TABLE: Project (Identification of this Armadillo project)
--
CREATE TABLE Project
(
	Project_id INTEGER PRIMARY KEY,
	ProjectName TEXT,
	Author TEXT,
	Institution TEXT,
        email TEXT,
	Note TEXT,
	DateCreated TEXT
);

--
-- CREATE TABLE: Properties. Properties of differents object/Program
--
CREATE TABLE Properties
(
	properties_id INTEGER NOT NULL,
	properties_name VARCHAR(200),
	PRIMARY KEY (properties_id)
);

--
-- CREATE Table PropertiesValues. Key and Values associated to a Properties_ID
--
CREATE TABLE PropertiesValues 
(
	properties_id INTEGER NOT NULL,
	Key_name VARCHAR(200),
	Key_value VARCHAR(200),
	FOREIGN KEY (properties_id) REFERENCES Properties(properties_id) ON DELETE CASCADE
);

--
-- CREATE TABLE: RunPrograms 
--
CREATE TABLE RunPrograms
(
	runProgram_id INTEGER NOT NULL,
	properties_id INTEGER,
	workflows_id INTEGER,
	runProgramOutput TEXT,
	note TEXT,
	programTimeStart TIME,
	programTimeEnd TIME,
	status INTEGER,
	PRIMARY KEY (runProgram_id)
);

--
-- CREATE TABLE: Input
--
CREATE TABLE Input
(
	input_id INTEGER NOT NULL,
        runProgram_id INTEGER NOT NULL,
        Type TEXT,
        TypeID INTEGER DEFAULT 0
);

--
-- CREATE TABLE: Output
--
CREATE TABLE Output
(
	output_id INTEGER NOT NULL,
        runProgram_id INTEGER NOT NULL,
        Type TEXT,
        TypeID INTEGER DEFAULT 0
);

--
-- CREATE TABLE: Workflows
--
-- Note: Etienne Lord
CREATE TABLE Workflows
(
	workflows_id INTEGER NOT NULL,
	workflows_name VARCHAR(255),
	workflows_filename VARCHAR(255),
	workflow_in_txt TEXT,
	note TEXT,
        displayLINE BOOLEAN DEFAULT True,
        simpleGraph BOOLEAN DEFAULT False,
	date_created TIME,
	date_modified TIME,
        workflows_outputText TEXT,
	PRIMARY KEY (workflows_id)
);

--
-- CREATE TABLE: RunWorkflow
--
-- Note: Etienne Lord
CREATE TABLE RunWorkflow
(
	RunWorkflow_id INTEGER NOT NULL,
        name TEXT, 
        note TEXT,
        original_id INTEGER DEFAULT 0,
        execution_id INTEGER DEFAULT 0,
	complete BOOLEAN DEFAULT FALSE
);


--
-- CREATE TABLE: Sequence, contain original sequences from genome databases. Id's databases correspond to access numbers.
-- Note: abbreviate is a name compatible with the phylip format (MAX 9 character)
-- NOTE: sequence_type IS DNA, RNA, AA....
CREATE TABLE Sequence
(
	sequence_id INTEGER NOT NULL,
	name TEXT,
        quality TEXT,
	abbreviate TEXT,
	accession TEXT,
	accessionReferee TEXT,
	gi TEXT,
	sequence TEXT,
	sequence_len INTEGER,
	sequence_type TEXT,
	note TEXT,
	timeAdded TIME,
	filename TEXT,
	runProgram_id INTEGER DEFAULT 0,
	PRIMARY KEY (sequence_id)
);

--
-- CREATE TABLE: NonAlignedSequences, contain several original sequences non aligned. Name can be considered as a filename.
-- Note: the multipleSequences_id is not UNIQUE
-- MultipleSequences
CREATE TABLE MultipleSequences
(
	multipleSequences_id INTEGER NOT NULL,
        sequence_id INTEGER NOT NULL,
	name VARCHAR(50),
	note TEXT,
	runProgram_id INTEGER DEFAULT 0,
	FOREIGN KEY (sequence_id) REFERENCES Sequence (sequence_id) ON DELETE CASCADE
);
--CREATE INDEX NonAlignedSequences_index ON NonAlignedSequences (nonAlignedSequence_id);

--
-- CREATE TABLE: AlignedSequences, contain several aligned sequences after alignement in a program. Name can be considered as a filename where all aligned sequence from the same alignedSequence_id group are gathered. Sequence is a sequence aligned with others from the same alignedSequence_id group.
-- Note: the alignment_id is not UNIQUE
-- Note: sequence_id is this sequence original ID before alignment
-- Note: sequenceStats_id is the new alignedSequenceStats
CREATE TABLE Alignment
(
	alignment_id INTEGER NOT NULL,
	sequence_id  INTEGER NOT NULL,
	original_sequence_id INTEGER NOT NULL,
	runProgram_id INTEGER DEFAULT 0,
	name VARCHAR(50),
	note TEXT,
	FOREIGN KEY (runProgram_id) REFERENCES RunPrograms (runProgram_id) ON DELETE CASCADE,
	FOREIGN KEY (sequence_id) REFERENCES Sequence (sequence_id) ON DELETE CASCADE
);
--CREATE INDEX AlignedSequences_index ON AlignedSequences (AlignedSequence_id);

--
--  CREATE TABLE:MultipleAlignments. Contain an aggregation of Alignment
--
CREATE TABLE MultipleAlignments
(
	multipleAlignments_id INTEGER NOT NULL,
        alignment_id INTEGER NOT NULL,
	name VARCHAR(50),
	note TEXT,
	runProgram_id INTEGER DEFAULT 0,
	FOREIGN KEY (alignment_id) REFERENCES Alignment (alignment_id) ON DELETE CASCADE
);

--
-- CREATE TABLE: Trees. Contain all trees with the program used to make them
--
CREATE TABLE Tree
(
	tree_id INTEGER NOT NULL,
	runProgram_id INTEGER DEFAULT 0,
	tree TEXT,
	treeSequenceID TEXT,
	treeAbbreviate TEXT,
	name TEXT,
        note TEXT,
	rooted BOOLEAN DEFAULT FALSE,
	PRIMARY KEY (tree_id),
	FOREIGN KEY (runProgram_id) REFERENCES RunPrograms (runProgram_id) ON DELETE CASCADE
);

--
--  CREATE TABLE:MultipleTrees. Contain an aggregation of Trees
--
CREATE TABLE MultipleTrees
(
	multipleTrees_id INTEGER NOT NULL,
        tree_id INTEGER NOT NULL,
	name VARCHAR(50),
	note TEXT,
	runProgram_id INTEGER DEFAULT 0,
	FOREIGN KEY (tree_id) REFERENCES Tree (tree_id) ON DELETE CASCADE
);

--
-- CREATE TABLE: Ancestors. Contain ancestors
--
CREATE TABLE Ancestor
(
	ancestor_id INTEGER NOT NULL,
        sequence_id INTEGER NOT NULL,
	alignment_id INTEGER,
        tree_id INTEGER,
	runProgram_id INTEGER DEFAULT 0,
	ancestor TEXT,
	Name TEXT,
        Note TEXT
);

--
-- CREATE TABLE: Matrix
--
CREATE TABLE Matrix
(
	matrix_id INTEGER NOT NULL,
	matrix TEXT,
	Name TEXT,
	Note TEXT,
	runProgram_id INTEGER DEFAULT 0,
	PRIMARY KEY (matrix_id)
);

--
-- CREATE TABLE: Phylip
--
CREATE TABLE Phylip
(
	phylip_id INTEGER NOT NULL,
	phylip_data TEXT,
	phylip_datatype TEXT,
	Name TEXT,
	Note TEXT,
	runProgram_id INTEGER DEFAULT 0,
	PRIMARY KEY (phylip_id)
);

--
-- CREATE TABLE: Unknown
--
CREATE TABLE Unknown
(
	Unknown_id INTEGER NOT NULL,
	Unknown TEXT,
	Name TEXT,
	filename TEXT,
	Note TEXT,
	UnknownType TEXT,
	runProgram_id INTEGER DEFAULT 0,
	PRIMARY KEY (Unknown_id)
);

--
-- CREATE TABLE: BlastHit
--
CREATE TABLE BlastHit
(
    BlastHit_id INTEGER NOT NULL,
    BlastHitList_id INTEGER NOT NULL,
    dbname TEXT,
    subject_id INTEGER NOT NULL,
	subject_id_gi TEXT,
	subject_accession TEXT,
	subject_accession_referree TEXT,
        subject_name TEXT,
        subject_length FLOAT,
        sstart FLOAT,
	ssend FLOAT,
        subject_sequence TEXT,
    query_id INTEGER NOT NULL,
        query_name TEXT,
        qstrand TEXT,
	sstrand TEXT,
        qstart FLOAT,
	qend FLOAT,
	evalue DOUBLE,
	bitscore DOUBLE,
	identify FLOAT,
	alignment_length FLOAT,
        query_length FLOAT,
        query_sequence TEXT,
    positives FLOAT,
    missmatches FLOAT,
    gap FLOAT,
    score FLOAT,
    runProgram_id INTEGER DEFAULT 0,
    PRIMARY KEY (BlastHit_id)
);

--
-- CREATE TABLE: Annotation
--
CREATE TABLE ANNOTATION
(
    Annotation_id  INTEGER NOT NULL,
    Annotation       TEXT,
    AnnotationSub    TEXT,
    Species          TEXT,
    AnnotationType   TEXT,
    AnnotationStrand TEXT,
    AnnotationFrame  INTEGER,
    AnnotationGroup  TEXT,
    AnnotationGroupName TEXT,
    AnnotationColor   TEXT,
    AnnotationStart   INTEGER,
    AnnotationEnd     INTEGER,
    AnnotationOrientation TEXT,
    AnnotationComment    TEXT,
    AnnotationSourceType  TEXT,
    AnnotationSourceID    INTEGER,
    Score                 REAL,
    DateAdded     DATETIME
);

--
-- CREATE TABLE: Genome
-- Source is the type (MultipleSequence, Alignment, Annotation)
--
CREATE TABLE Genome
(
    GenomeID  INTEGER NOT NULL,
    Name       TEXT,
    Length     INTEGER,
    Start         INTEGER,
    End           INTEGER,
    PositionStart INTEGER,
    PositionEnd   INTEGER,
    Orientation   TEXT,
    Source        TEXT,
    SourceID      INTEGER,
    Comment       TEXT,
    DateAdded     DATETIME
);