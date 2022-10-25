# **Guce eForex SQL Scripts**
Latest first.

## version 3.0.6
## [GC-514] [e-Forex Cession] Pre-financing in ceded status without EC
    alter table CLEARANCE add CEDED number(1) default '0';
    alter table CLEARANCE_DOMICILIATION add REPAT_CLEARANCE number(19,0);

## [GC-504] [e-Forex Cession] Currency transfer of pre-financing without EC
    alter table CURRENCY_TRANSFER add IS_PREFINANCING_WITHOUTEC number(1) default '0';
    
## [GC-498] [eForex] Improve the length of the docTypName
    ALTER TABLE ATTACH_CURRENCY_TRANSFER  MODIFY (DOC_TYP_NAME VARCHAR2(100 CHAR) );
    ALTER TABLE ATTACH_TRANSFER  MODIFY (DOC_TYP_NAME VARCHAR2(100 CHAR) );
    ALTER TABLE ATTACH_REP  MODIFY (DOC_TYP_NAME VARCHAR2(100 CHAR) );
    ALTER TABLE ATTACHED_DOC  MODIFY (DOC_TYP_NAME VARCHAR2(100 CHAR) );

## [GC-430] [eForex - Rapatriement] Improvement on delete EC in section Clearance of domiciliations.
    alter table CLEARANCE add status number(1) default '1';

## version 3.0.5

## [CIV-5549] Simplified declaration for EA
    alter table EXCHANGE add SPECIAL_IMPORTER number(1) default '0';
	
## [CIV-5461] eFOREX : module improvment
    alter table EXCHANGE add IS_RELEASE_ORDER number(1);
	
## version 3.0.4
## [CIV-4720] Create a new field "area"
    alter table EXCHANGE add AREA_CODE varchar2(5 char)    
    alter table EXCHANGE add AREA_NAME varchar2(75 char)
    
## [CIV-4722] Create a new field "country"
    alter table EXCHANGE add COUNTRY_CODE varchar2(5 char) 
    alter table EXCHANGE add COUNTRY_NAME varchar2(75 char)
    	
    	   
 	   	

## version 3.0.2
## [CIV-4365]
    ALTER TABLE CURRENCY_TRANSFER ADD TRANSFER_RATE NUMBER(19,3);

## version 3.0.0-RC20
## [CIV-3166]
     ALTER TABLE CURRENCY_TRANSFER ADD REPAT_NO VARCHAR2(11);
     ALTER TABLE CURRENCY_TRANSFER ADD REPAT_DATE DATE;
     ALTER TABLE CURRENCY_TRANSFER ADD AMOUNT_REPAT NUMBER(19,2);
     ALTER TABLE CURRENCY_TRANSFER ADD AMOUNT_REPAT_NAT NUMBER(19,2);
    /
     
## version 3.0.0-RC19
## [CIV-2892]
    alter table EXECUTION
    	add state varchar2(10) default "0";

## version 3.0.0-RC19
## [CIV-2789]
    alter table TRANSFER_ORDER
    	add SUM_OF_CLEAREANCE number(19,2);
    /

## version 3.0.0-RC18
## [CIV-2272]
    INSERT INTO CI_GIM.SYSTEM_INFO (ID, APP_NAME, DURATION, MSG, MSG_FR, STATUS, VALID_FROM, VALID_TO) 
    VALUES (1004, 'efem', 1500000, 'For all requests sent to the SGBCI bank , please use the code : SGCI', 'Pour vos demandes adressées à la banque SGBCI, veuillez utiliser le code : SGCI', 'Valid', TO_TIMESTAMP('2020-11-19 09:36:29.948000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2024-12-31 09:36:29.950000', 'YYYY-MM-DD HH24:MI:SS.FF6'))

## version 3.0.0-RC18
### [CIV-2254,CIV-2255]
    alter table EXCHANGE
    	add DEC_RMG_BAL_AMT number(19,3);
    /
    
    alter table EXCHANGE
    	add DEC_RMG_BAL_AMT_NAT number(19,3);
    /
    
    alter table EXCHANGE
    	add DEC_RMG_BAL_TRANS_AMT number(19,3);
    /
    
    alter table EXCHANGE
    	add DEC_RMG_BAL_TRANS_AMT_NAT number(19,3);
    /
    
    alter table SUP_DECLARATION
    	add DECLARATION_REMAINING_BALANCE number(19,3);
    /
    
    alter table SUP_DECLARATION
    	add DECLARATION_CIF_AMOUNT NUMBER(19,3);
    /
    
    alter table SUP_DECLARATION
    	add DECLARATION_AMOUNT_WRITE_OFF number(19,3);
    /

## version "3.0.0-RC16"
### [CIV-1789](https://jira.webbfontaine.com/browse/CIV-1789) - [EA] Missing link/button to download attachments for all status.
    
    ALTER TABLE ATTACH_TRANSFER ADD ATT_FIL NUMBER(19,0);
    
    ALTER TABLE ATTACH_TRANSFER
    ADD CONSTRAINT ATT_FIL_T_FK FOREIGN KEY
    (
      ATT_FIL 
    )
    REFERENCES ATTACHED_FILE
    (
      ID 
    )
    ENABLE;
    
    ALTER TABLE ATTACH_CURRENCY_TRANSFER ADD ATT_FIL NUMBER(19,0);
    
    ALTER TABLE ATTACH_CURRENCY_TRANSFER
    ADD CONSTRAINT ATT_FIL_CT_FK FOREIGN KEY
    (
      ATT_FIL 
    )
    REFERENCES ATTACHED_FILE
    (
      ID 
    )
    ENABLE;

### [CIV-1695](https://jira.webbfontaine.com/browse/CIV-1695) - ADD NEW COLUMN NAME EML_EC IN RIMM_BNKEML
       
       ALTER TABLE RIMM_BNK_EML ADD EML_EC VARCHAR(128 BYTE);
       
### [CIV-607](https://jira.webbfontaine.com/browse/CIV-607) - Currencies Transfer Workflow
       
       ALTER TABLE CURRENCY_TRANSFER ADD LAST_TRANSACTION_DATE TIMESTAMP(6);
       ALTER TABLE CURRENCY_TRANSFER MODIFY BANK_CODE VARCHAR2(7 CHAR);

### [CIV-651](https://jira.webbfontaine.com/browse/CIV-651) - Add domain class
**Datasource**
*EFOREX*

      CREATE TABLE "CURRENCY_TRANSFER_RECORD" 
       (	"ID" NUMBER(19,0), 
        "VERSION" NUMBER(19,0), 
        "DOCUMENT_ID" NUMBER(19,0), 
        "USER_LOGIN" VARCHAR2(255 CHAR), 
        "OPERATION" VARCHAR2(255 CHAR), 
        "END_STATUS" VARCHAR2(255 CHAR), 
        "OPERATION_DATE" TIMESTAMP (6), 
        "LOG_MESSAGE" CLOB, 
        "INITIAL_STATUS" VARCHAR2(255 CHAR)
       ) ;
    
      CREATE TABLE "CLEARANCE_DOMICILIATION" 
       (	"ID" NUMBER(19,0), 
        "VERSION" NUMBER(19,0), 
        "CURRENCY_TRANSFER_ID" NUMBER(19,0), 
        "EC_DATE" DATE, 
        "RANK" NUMBER(10,0), 
        "DOM_CODE_BANK" VARCHAR2(5 CHAR), 
        "DOM_DAT" DATE, 
        "AMT_TRANSF_CURR" NUMBER(19,2), 
        "EC_REF" VARCHAR2(11 CHAR), 
        "INV_AMT_CURR" NUMBER(19,2), 
        "DOM_CUR" NUMBER(19,2), 
        "REPA_AMT_CURR" NUMBER(19,2), 
        "DOM_NO" VARCHAR2(35 CHAR), 
        "CLEARANCE_DOMICILIATIONS_IDX" NUMBER(10,0)
       ) ;
    
      CREATE TABLE "ATTACH_CURRENCY_TRANSFER" 
       (	"ID" NUMBER(19,0), 
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
        "ATTACHED_DOCS_IDX" NUMBER(10,0)
       ) ;
    
      CREATE TABLE "CURRENCY_TRANSFER" 
       (	"ID" NUMBER(19,0), 
        "VERSION" NUMBER(19,0), 
        "REQUEST_DATE" DATE, 
        "AMOUN_TR_DAT" NUMBER(19,2), 
        "CUR_RATE" NUMBER(19,3), 
        "BANK_CODE" VARCHAR2(5 CHAR), 
        "REQ_YER" NUMBER(10,0), 
        "AMOUN_TRAN" NUMBER(19,2), 
        "CUR_NAME" VARCHAR2(35 CHAR), 
        "REQUEST_NO" VARCHAR2(11 CHAR), 
        "CURRENCY_CODE" VARCHAR2(3 CHAR), 
        "STATUS" VARCHAR2(30 CHAR), 
        "CUR_TR_DAT" DATE, 
        "REQ_SEQ" NUMBER(10,0), 
        "BANK_NAME" VARCHAR2(35 CHAR)
       ) ;
    
      CREATE UNIQUE INDEX "SYS_C0011219" ON "CURRENCY_TRANSFER_RECORD" ("ID") 
      ;
    
      CREATE UNIQUE INDEX "SYS_C0011215" ON "CLEARANCE_DOMICILIATION" ("ID") 
      ;
    
      CREATE UNIQUE INDEX "SYS_C0011203" ON "ATTACH_CURRENCY_TRANSFER" ("ID") 
      ;
    
      CREATE UNIQUE INDEX "SYS_C0011217" ON "CURRENCY_TRANSFER" ("ID") 
      ;
    
      ALTER TABLE "CURRENCY_TRANSFER_RECORD" MODIFY ("ID" NOT NULL ENABLE);
     
      ALTER TABLE "CURRENCY_TRANSFER_RECORD" ADD PRIMARY KEY ("ID") ENABLE;
    
      ALTER TABLE "CLEARANCE_DOMICILIATION" MODIFY ("ID" NOT NULL ENABLE);
     
      ALTER TABLE "CLEARANCE_DOMICILIATION" ADD PRIMARY KEY ("ID") ENABLE;
    
      ALTER TABLE "ATTACH_CURRENCY_TRANSFER" MODIFY ("ID" NOT NULL ENABLE);
     
      ALTER TABLE "ATTACH_CURRENCY_TRANSFER" ADD PRIMARY KEY ("ID") ENABLE;
    
      ALTER TABLE "CURRENCY_TRANSFER" MODIFY ("ID" NOT NULL ENABLE);
     
      ALTER TABLE "CURRENCY_TRANSFER" ADD PRIMARY KEY ("ID") ENABLE;
    
      ALTER TABLE "CURRENCY_TRANSFER_RECORD" ADD CONSTRAINT "FKIGAT6IY1FJA3CXEW22VYCKR7K" FOREIGN KEY ("DOCUMENT_ID")
          REFERENCES "CURRENCY_TRANSFER" ("ID") ENABLE;
    
      ALTER TABLE "CLEARANCE_DOMICILIATION" ADD CONSTRAINT "FKR0AEW8OR7G5WMAQKPMV23VW89" FOREIGN KEY ("CURRENCY_TRANSFER_ID")
          REFERENCES "CURRENCY_TRANSFER" ("ID") ENABLE;
    
      ALTER TABLE "ATTACH_CURRENCY_TRANSFER" ADD CONSTRAINT "FKC4LU0M1WRDJG1LQ3SSK9S5LV9" FOREIGN KEY ("CURRENCY_TRANSFER_ID")
          REFERENCES "CURRENCY_TRANSFER" ("ID") ENABLE;
	  
