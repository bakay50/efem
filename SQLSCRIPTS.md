
**CIV-2758 / Add a new column named Exporter Name

    alter table CLEARANCE_DOMICILIATION add EC_EXPORTER_NAME CLOB

**CIV-2030: The cancellation of a repatriation is possible without giving a reason.**

    ALTER TABLE REPATRIATION ADD COMMENTS CLOB;

**CIV-1221: Unable to create a repatriation file**

    ALTER TABLE REPATRIATION
    ADD CONSTRAINT REPATRIATION_PK PRIMARY KEY 
    (
      ID 
    )
    ENABLE;
        
    ALTER TABLE ATTACH_REP 
    DROP CONSTRAINT SYS_C00143443;
        
    ALTER TABLE ATTACH_REP
    ADD CONSTRAINT SYS_C00143443 FOREIGN KEY
    (
      REPATS_ID 
    )
    REFERENCES REPATRIATION
    (
      ID 
    )
    ENABLE;
        
    ALTER TABLE CLEARANCE 
    DROP CONSTRAINT SYS_C00143441;
        
    ALTER TABLE CLEARANCE
    ADD CONSTRAINT SYS_C00143441 FOREIGN KEY
    (
      REPATS_ID 
    )
    REFERENCES REPATRIATION
    (
      ID 
    )
    ENABLE;
        
    ALTER TABLE REP_TRANSACTION_RECORD 
    DROP CONSTRAINT FKNHPS1Q1ID14Y6DWXLFK8R5KID;
           
    ALTER TABLE REP_TRANSACTION_RECORD
    ADD CONSTRAINT FKNHPS1Q1ID14Y6DWXLFK8R5KID FOREIGN KEY
    (
      DOCUMENT_ID 
    )
    REFERENCES REPATRIATION
    (
      ID 
    )
    ENABLE;

**CIV-962: Message Test should be mandatory on Cancel operation**

    ALTER TABLE CURRENCY_TRANSFER ADD COMMENTS CLOB; 

**CIV-1235: Error when clicking on the Search button**

    CREATE TABLE "ATTACH_CURRENCY_TRANSFER" 
    ("ID" NUMBER(19,0), 
    "VERSION" NUMBER(19,0), 
    "DOC_CODE" VARCHAR2(5 CHAR), 
    "CURRENCY_TRANSFER_ID" NUMBER(19,0), 
    "DOC_TYP_NAME" VARCHAR2(70 CHAR), 
    "RANK" NUMBER(10,0), 
    "FILE_EXTENSION" VARCHAR2(255 CHAR), 
    "DOC_DATE" DATE, 
    "DOC_REF" VARCHAR2(35 CHAR), 
    "UPL_FIL" BLOB, 
    "IS_UPDATED" NUMBER(1,0), 
    "ATTACHED_DOCS_IDX" NUMBER(10,0),
    PRIMARY KEY(ID)
    );
    
    CREATE TABLE "CLEARANCE_DOMICILIATION" 
    ("ID" NUMBER(19,0), 
    "VERSION" NUMBER(19,0), 
    "CURRENCY_TRANSFER_ID" NUMBER(19,0), 
    "EC_DATE" DATE, 
    "RANK" NUMBER(10,0), 
    "DOM_CODE_BANK" VARCHAR2(5 CHAR), 
    "DOM_DAT" DATE, 
    "AMT_TRANSF_CURR" NUMBER(19,2), 
    "EC_REF" VARCHAR2(14 CHAR), 
    "INV_AMT_CURR" NUMBER(19,2), 
    "DOM_CUR" NUMBER(19,2), 	
    "REPA_AMT_CURR" NUMBER(19,2), 
    "DOM_NO" VARCHAR2(35 CHAR), 
    "CLEARANCE_DOMICILIATIONS_IDX" NUMBER(10,0),
    PRIMARY KEY(ID)
    );
    
    CREATE TABLE "CURRENCY_TRANSFER" 
    ("ID" NUMBER(19,0), 
    "VERSION" NUMBER(19,0), 
    "LAST_TRANSACTION_DATE" TIMESTAMP (6), 
    "USER_OWNER" VARCHAR2(255 CHAR), 
    "REQUEST_DATE" DATE, 
    "AMOUN_TR_DAT" NUMBER(19,2), 
    "CUR_RATE" NUMBER(19,3), 
    "BANK_CODE" VARCHAR2(7 CHAR), 
    "REQ_YER" NUMBER(10,0), 
    "AMOUN_TRAN" NUMBER(19,2), 
    "CUR_NAME" VARCHAR2(35 CHAR), 
    "REQUEST_NO" VARCHAR2(11 CHAR), 
    "CURRENCY_CODE" VARCHAR2(3 CHAR), 
    "STATUS" VARCHAR2(30 CHAR), 
    "CUR_TR_DAT" DATE, 
    "REQ_SEQ" NUMBER(10,0), 
    "BANK_NAME" VARCHAR2(35 CHAR),
    PRIMARY KEY(ID)
    );
    
    CREATE TABLE "CURRENCY_TRANSFER_RECORD" 
    ("ID" NUMBER(19,0), 
    "VERSION" NUMBER(19,0), 
    "DOCUMENT_ID" NUMBER(19,0), 
    "USER_LOGIN" VARCHAR2(255 CHAR), 
    "OPERATION" VARCHAR2(255 CHAR), 
    "END_STATUS" VARCHAR2(255 CHAR), 
    "OPERATION_DATE" TIMESTAMP (6), 
    "LOG_MESSAGE" CLOB, 
    "INITIAL_STATUS" VARCHAR2(255 CHAR),
    PRIMARY KEY(ID)
    );
    
**CIV-462: As a Trader, I should be able to perform Validate on requested status**

    ALTER TABLE EXECUTION  
    MODIFY (ACCOUNT_OWNER_CREDITED NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (CURRENCY_EX_RATE NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (CTY_PROV_DES_EX_COD NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (AMT_SET_NAT_EX_CUR NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (ACCOUNT_EX_BENEFICIARY NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (CREDIT_CORRESPONDENT_ACCOUNT NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (CREDIT_FOREIGN_CFA_OR_EURO NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (ACCOUNT_OWNER_CREDITED NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (EXECUTING_BANK_CODE NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (EXECUTING_BANK_NAME NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (EXECUTION_DOM_NUMBER NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (EXECUTION_DOM_DAT NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (EXECUTION_DOM_COD NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (EXECUTION_DOM_NAM NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (CURRENCY_EX_RATE NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (CTY_PROV_DES_EX_COD NULL);
    
    ALTER TABLE EXECUTION  
    MODIFY (AMT_SET_NAT_EX_CUR NULL);


**CIV-478 State business requirement**
    ALTER TABLE TRANSFER_ORDER_CLEARANCE MODIFY STATE DEFAULT '0'
**Create and Update All Table for Transfer Order**

     -- ----------------------------
     -- Table structure for ATTACH_TRANSFER
     -- ----------------------------
     DROP TABLE "ATTACH_TRANSFER";
     CREATE TABLE "ATTACH_TRANSFER" (
     "ID" NUMBER(19) NOT NULL ,
     "VERSION" NUMBER(19) NULL ,
     "DOC_CODE" VARCHAR2(5 CHAR) NULL ,
     "TRANSFER_ID" NUMBER(19) NULL ,
     "DOC_TYP_NAME" VARCHAR2(70 CHAR) NULL ,
     "RANK" NUMBER(10) NULL ,
     "FILE_EXTENSION" VARCHAR2(255 CHAR) NULL ,
     "DOC_DATE" DATE NULL ,
     "DOC_REF" VARCHAR2(35 CHAR) NULL ,
     "UPL_FIL" BLOB NULL ,
     "IS_UPDATED" NUMBER(1) NULL ,
     "ATTACHED_DOCS_IDX" NUMBER(10) NULL
     )
     LOGGING
     NOCOMPRESS
     NOCACHE

     ;

     -- ----------------------------
     -- Table structure for TRANSACTION_RECORD
     -- ----------------------------
     DROP TABLE "TRANSACTION_RECORD";
     CREATE TABLE "TRANSACTION_RECORD" (
     "ID" NUMBER(19) NOT NULL ,
     "VERSION" NUMBER(19) NULL ,
     "USER_LOGIN" VARCHAR2(255 CHAR) NULL ,
     "OPERATION" VARCHAR2(255 CHAR) NULL ,
     "DOCUMENT_ID" NUMBER(19) NULL ,
     "END_STATUS" VARCHAR2(255 CHAR) NULL ,
     "OPERATION_DATE" TIMESTAMP(6)  NULL ,
     "LOG_MESSAGE" CLOB NULL ,
     "INITIAL_STATUS" VARCHAR2(255 CHAR) NULL
     )
     LOGGING
     NOCOMPRESS
     NOCACHE

     ;

     -- ----------------------------
     -- Table structure for TRANSFER_ORDER
     -- ----------------------------
     DROP TABLE "TRANSFER_ORDER";
     CREATE TABLE "TRANSFER_ORDER" (
     "ID" NUMBER(19) NOT NULL ,
     "VERSION" NUMBER(19) NULL ,
     "LAST_TRANSACTION_DATE" TIMESTAMP(6)  NULL ,
     "COUNTRY_BENEF_BANK_NAME" VARCHAR2(35 CHAR) NULL ,
     "USER_GROUP" VARCHAR2(255 CHAR) NULL ,
     "TRANSF_AMT_REQSTED" NUMBER(19,2) NULL ,
     "TRANSF_NAT_AMT_REQSTED" NUMBER(19,2) NULL ,
     "EA_REFERENCE" VARCHAR2(255 CHAR) NULL ,
     "STORE_OWNER" VARCHAR2(255 CHAR) NULL ,
     "USER_OWNER" VARCHAR2(255 CHAR) NULL ,
     "CURR_COD_PAY" VARCHAR2(3 CHAR) NULL ,
     "REQUEST_DATE" DATE NULL ,
     "NAME_OF_ACCNT_HOLD_CREDIT" VARCHAR2(255 CHAR) NULL ,
     "DESTINATION_BANK" VARCHAR2(100 CHAR) NULL ,
     "BANK_ACCNT_NO_DEBITED" VARCHAR2(34 CHAR) NULL ,
     "RATE_PAYMENT" NUMBER(19,3) NULL ,
     "COUNTRY_BENEF_BANK_CODE" VARCHAR2(2 CHAR) NULL ,
     "BANK_CODE" VARCHAR2(5 CHAR) NULL ,
     "EXEC_DAT" DATE NULL ,
     "AMT_SET_MENT_CUR" NUMBER(19,2) NULL ,
     "AMT_REQSTED_LETTER" VARCHAR2(255 CHAR) NULL ,
     "BANK_ACCNT_NO_CREDIT" VARCHAR2(34 CHAR) NULL ,
     "REQ_YER" NUMBER(10) NULL ,
     "IMP_NAME_ADDR" CLOB NULL ,
     "IMP_CODE" VARCHAR2(17 CHAR) NULL ,
     "BY_CREDIT_OF_ACCNT_OF_CORSP" VARCHAR2(255 CHAR) NULL ,
     "AMT_EXECTED_LETTER" VARCHAR2(255 CHAR) NULL ,
     "TRANSF_NAT_AMT_EXECTED" NUMBER(19,2) NULL ,
     "CURR_NAME_PAY" VARCHAR2(35 CHAR) NULL ,
     "EXEC_REF" VARCHAR2(255 CHAR) NULL ,
     "REQUEST_NO" VARCHAR2(11 CHAR) NULL ,
     "STATUS" VARCHAR2(20 CHAR) NULL ,
     "CHARGES" VARCHAR2(65 CHAR) NULL ,
     "TRANSF_AMT_EXECTED" NUMBER(19,2) NULL ,
     "REQ_SEQ" NUMBER(10) NULL ,
     "BANK_NAME" VARCHAR2(35 CHAR) NULL
     )
     LOGGING
     NOCOMPRESS
     NOCACHE

     ;

     -- ----------------------------
     -- Table structure for TRANSFER_ORDER_CLEARANCE
     -- ----------------------------
     DROP TABLE "TRANSFER_ORDER_CLEARANCE";
     CREATE TABLE "TRANSFER_ORDER_CLEARANCE" (
     "ID" NUMBER(19) NOT NULL ,
     "VERSION" NUMBER(19) NULL ,
     "EA_REFERENCE" VARCHAR2(11 CHAR) NULL ,
     "RANK" NUMBER(10) NULL ,
     "AMOUNT_SETTLED" NUMBER(19,2) NULL ,
     "REG_NO_BANK" VARCHAR2(255 CHAR) NULL ,
     "BANK_CODE" VARCHAR2(255 CHAR) NULL ,
     "STATE" VARCHAR2(255 CHAR) NULL ,
     "AUTH_DATE" DATE NULL ,
     "AMOUNT_REQUESTED" NUMBER(19,2) NULL ,
     "TRANSFER_ID" NUMBER(19) NULL ,
     "REG_DATE_BANK" DATE NULL ,
     "AMOUNT_TOBE_SETTLED" NUMBER(19,2) NULL ,
     "BANK_NAME" VARCHAR2(255 CHAR) NULL ,
     "ORDER_CLEARANCE_OF_DOMS_IDX" NUMBER(10) NULL
     )
     LOGGING
     NOCOMPRESS
     NOCACHE

     ;

     -- ----------------------------
     -- Table structure for TRANSFER_TRANSACT_RECORD
     -- ----------------------------
     DROP TABLE "TRANSFER_TRANSACT_RECORD";
     CREATE TABLE "TRANSFER_TRANSACT_RECORD" (
     "ID" NUMBER(19) NOT NULL ,
     "VERSION" NUMBER(19) NULL ,
     "USER_LOGIN" VARCHAR2(255 CHAR) NULL ,
     "OPERATION" VARCHAR2(255 CHAR) NULL ,
     "END_STATUS" VARCHAR2(255 CHAR) NULL ,
     "DOCUMENT_ID" NUMBER(19) NULL ,
     "OPERATION_DATE" TIMESTAMP(6)  NULL ,
     "LOG_MESSAGE" CLOB NULL ,
     "INITIAL_STATUS" VARCHAR2(255 CHAR) NULL
     )
     LOGGING
     NOCOMPRESS
     NOCACHE

     ;

     -- ----------------------------
     -- Indexes structure for table ATTACH_TRANSFER
     -- ----------------------------

     -- ----------------------------
     -- Checks structure for table ATTACH_TRANSFER
     -- ----------------------------
     ALTER TABLE "ATTACH_TRANSFER" ADD CHECK ("ID" IS NOT NULL);

     -- ----------------------------
     -- Primary Key structure for table ATTACH_TRANSFER
     -- ----------------------------
     ALTER TABLE "ATTACH_TRANSFER" ADD PRIMARY KEY ("ID");

     -- ----------------------------
     -- Indexes structure for table TRANSACTION_RECORD
     -- ----------------------------

     -- ----------------------------
     -- Checks structure for table TRANSACTION_RECORD
     -- ----------------------------
     ALTER TABLE "TRANSACTION_RECORD" ADD CHECK ("ID" IS NOT NULL);

     -- ----------------------------
     -- Primary Key structure for table TRANSACTION_RECORD
     -- ----------------------------
     ALTER TABLE "TRANSACTION_RECORD" ADD PRIMARY KEY ("ID");

     -- ----------------------------
     -- Indexes structure for table TRANSFER_ORDER
     -- ----------------------------

     -- ----------------------------
     -- Checks structure for table TRANSFER_ORDER
     -- ----------------------------
     ALTER TABLE "TRANSFER_ORDER" ADD CHECK ("ID" IS NOT NULL);

     -- ----------------------------
     -- Primary Key structure for table TRANSFER_ORDER
     -- ----------------------------
     ALTER TABLE "TRANSFER_ORDER" ADD PRIMARY KEY ("ID");

     -- ----------------------------
     -- Indexes structure for table TRANSFER_ORDER_CLEARANCE
     -- ----------------------------

     -- ----------------------------
     -- Checks structure for table TRANSFER_ORDER_CLEARANCE
     -- ----------------------------
     ALTER TABLE "TRANSFER_ORDER_CLEARANCE" ADD CHECK ("ID" IS NOT NULL);

     -- ----------------------------
     -- Primary Key structure for table TRANSFER_ORDER_CLEARANCE
     -- ----------------------------
     ALTER TABLE "TRANSFER_ORDER_CLEARANCE" ADD PRIMARY KEY ("ID");

     -- ----------------------------
     -- Indexes structure for table TRANSFER_TRANSACT_RECORD
     -- ----------------------------

     -- ----------------------------
     -- Checks structure for table TRANSFER_TRANSACT_RECORD
     -- ----------------------------
     ALTER TABLE "TRANSFER_TRANSACT_RECORD" ADD CHECK ("ID" IS NOT NULL);

     -- ----------------------------
     -- Primary Key structure for table TRANSFER_TRANSACT_RECORD
     -- ----------------------------
     ALTER TABLE "TRANSFER_TRANSACT_RECORD" ADD PRIMARY KEY ("ID");

     -- ----------------------------
     -- Foreign Key structure for table "ATTACH_TRANSFER"
     -- ----------------------------
     ALTER TABLE "ATTACH_TRANSFER" ADD FOREIGN KEY ("TRANSFER_ID") REFERENCES "TRANSFER_ORDER" ("ID");

     -- ----------------------------
     -- Foreign Key structure for table "TRANSACTION_RECORD"
     -- ----------------------------
     ALTER TABLE "TRANSACTION_RECORD" ADD FOREIGN KEY ("DOCUMENT_ID") REFERENCES "EXCHANGE" ("ID");

     -- ----------------------------
     -- Foreign Key structure for table "TRANSFER_ORDER_CLEARANCE"
     -- ----------------------------
     ALTER TABLE "TRANSFER_ORDER_CLEARANCE" ADD FOREIGN KEY ("TRANSFER_ID") REFERENCES "TRANSFER_ORDER" ("ID");

     -- ----------------------------
     -- Foreign Key structure for table "TRANSFER_TRANSACT_RECORD"
     -- ----------------------------
     ALTER TABLE "TRANSFER_TRANSACT_RECORD" ADD FOREIGN KEY ("DOCUMENT_ID") REFERENCES "TRANSFER_ORDER" ("ID");

     -- ----------------------------
     -- Update field EA_REFERENCE size for table "TRANSFER_ORDER_CLEARANCE"
     -- ----------------------------
     ALTER TABLE TRANSFER_ORDER_CLEARANCE RENAME COLUMN EA_REFERENCE TO EA_REFERENCEBIS;
     ALTER TABLE TRANSFER_ORDER_CLEARANCE ADD EA_REFERENCE VARCHAR2(20);
     UPDATE TRANSFER_ORDER_CLEARANCE SET EA_REFERENCE = EA_REFERENCEBIS;
     ALTER TABLE TRANSFER_ORDER_CLEARANCE DROP COLUMN EA_REFERENCEBIS;

##CIV-2012: Bank Management##
    
## Create the bank table
     CREATE TABLE "RIMM_BNK" 
        ("ID" NUMBER(19,0), 
        "COD" VARCHAR2(35 CHAR), 
        "DOV"  TIMESTAMP (6), 
        "EML" VARCHAR2(128 CHAR), 
        "EML_EC" VARCHAR2(128 CHAR), 
        "STATUS" VARCHAR2(15 CHAR), 
        PRIMARY KEY(ID));
    		
    
## Create the sequence for id Bank
    CREATE SEQUENCE RIMM_BNK_SEQ START WITH 1 INCREMENT BY 1;
