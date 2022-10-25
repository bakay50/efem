/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webbfontaine.efem.workflow

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 10/20/14
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */


public interface Operations {

    static final String OP_CONFIRM_DECLARE_QUERIED = "ConfirmDeclareQueried"
    static final String OP_CONFIRM_DECLARE_FROM_NEW  = "ConfirmDeclareFromNull"
    static final String OA_DECLARE_STORED = "declareStored"
    static final String OP_CONFIRM_STORED = "confirmStored"
    static final String OA_UPDATE_CLOSED  = "updateClosed"
    static final String OA_CANCEL_CONFIRMED = "cancelConfirmed"
    static final String OA_CLOSE_CONFIRMED = "closeConfirmed"
    static final String OA_UPDATE_CONFIRMED = "updateConfirmed"
    static final String OA_CONFIRM_DECLARED = "confirmDeclared"
    static final String OA_QUERY_DECLARED = "queryDeclared"
    static final String OA_REQUEST_STORED = "requestStored"
    static final String OA_UPDATE_STORED = "updateStored"
    static final String OA_DECLARE_FROM_NEW = "declareNew"
    static final String OA_STORE_FROM_NEW = "storeNew"
    static final String OP_STORE = "Store"
    static final String OP_VERIFY = "Verify"
    static final String OP_DECLARE = "Declare"
    static final String OP_CONFIRM = "Confirm"
    static final String OP_VALIDATE = "Validate"
    static final String OI_DIRECT_DELETE = "Delete"
    static final String OP_TRANSFER = "Transfer"

    static final String OI_UPDATE_STORED = "UpdateStored"
    static final String OI_DELETE_STORED = "DeleteStored"
    static final String OC_EDIT_STORED = "EditStored"
    static final String OC_EDIT_CONFIRMED = "EditConfirmed"
    static final String EDIT = "Edit"
    static final String OI_DECLARE_STORED = "DeclareStored"
    static final String OP_DELETE = "Delete"
    static final String OA_DELETE = "delete"
    static final String OA_DECLARE_QUERIED  = "declareQueried"
    static final String OI_QUERY_DECLARED = "QueryDeclared"
    static final String OI_CONFIRM_DECLARED = "ConfirmDeclared"
    static final String OA_CONFIRM = "confirm"
    static final String OI_UPDATE_CONFIRMED = "UpdateConfirmed"
    static final String OI_CANCEL_CONFIRMED = "CancelConfirmed"
    static final String OI_CLOSE_CONFIRMED = "CloseConfirmed"
    static final String OP_CLOSE = "Close"
    static final String OI_DECLARE_QUERIED = "DeclareQueried"
    static final String OI_UPDATE_CLOSED = "UpdateClosed"
    static final String OC_EDIT_CLOSED = "EditClosed"
    static final String OI_VIEW_STORED = "ViewStored"
    static final String OI_VIEW_DECLARED = "ViewDeclared"
    static final String OI_VIEW_CONFIRMED = "ViewConfirmed"
    static final String OI_VIEW_CLOSED = "ViewClosed"
    
    static final String OP_REQUEST = "Request"
    static final String OP_APPROVE = "Approve"
    static final String OP_DOMICILIATE = "Domiciliate"
    static final String OP_REJECT = "Reject"
    static final String OP_QUERY = "Query"
    static final String OP_UPDATE = "Update"
    static final String OP_CANCEL = "Cancel"
    static final String OP_CHECK = "Check"
    static final String OP_EXECUTE = "Execute"
    static final String OP_EDIT = "Edit"
    static final String OP_VIEW = "View"
    static final String OP_REQUEST_TRANSFER_ORDER = "RequestTransferOrder"
    static final String OP_REQUEST_CURRENCY_TRANSFER = "RequestCurrencyTransfer"
    static final String OP_UPDATE_CLEARANCE = "UpdateClearance"

    static final String OI_CANCEL = "Cancel"     
    static final String OI_CANCEL_APPROVED = "CancelApproved"
    static final String OI_REQUEST_FROM_NULL = "RequestFromNull"
    static final String OI_APPROVE = "ApproveFromRequested"
    static final String OI_DOMICILIATE = "DomiciliateFromRequest"
    static final String OI_APPROVE_PARTIAL_APPROVED = "ApproveFromPartialApproved"
    static final String OI_PARTIALAPPROVE_PARTIAL_APPROVED = "PartialApproveFromPartialApproved"
    static final String OI_PARTIAL_APPROVE = "PartialApprove"
    static final String OI_PARTIAL_APPROVE1 = "PartialApprove1"
     
    static final String OI_FULLY_APPROVE = "FullyApprove"
    static final String OI_REQUEST_QUERIED = "RequestQueried"
    static final String OI_UPDATE_QUERIED = "UpdateQueried"
    static final String OI_CANCEL_QUERIED = "CancelQueried"
    static final String OI_EXECUTE_APPROVED = "ExecuteApproved"
    static final String OI_UPDATE_APPROVED = "UpdateApproved"
    
    static final String OI_UPDATE_EXECUTED = "UpdateExecuted"
    static final String OI_UPDATE_REQUESTED = "UpdateRequested"
    static final String OI_EDIT_REQUESTED = "EditRequested"
    static final String OI_QUERY_REQUESTED = "QueryRequested"
    static final String OI_REJECT_PARTIAL_APPROVED = "RejectPartialApproved"
    static final String OI_QUERY_PARTIAL_APPROVED = "QueryPartialApproved"
        
   
    static final String OI_VIEW_REQUESTED = "ViewRequested"
    static final String OI_VIEW_CANCELED = "ViewCanceled"
    static final String OI_VIEW_APPROVED = "ViewApproved"
    static final String OI_VIEW_DOMICILIATED = "ViewDomiciliated"
    static final String OI_VIEW_PARTIAL_APPROVED = "ViewPartialApproved"
    static final String OI_VIEW_QUERIED = "ViewQueried"
    static final String OI_VIEW_REJECTED = "ViewRejected"
    static final String OI_VIEW_EXECUTED = "ViewExecuted"
    static final String OI_VIEW_UPDATED = "ViewUpdated"
  
    
    static final String OA_CREATE = "create"
    static final String OA_APPROVE = "approve"
    static final String OA_DOMICILIATE = "domiciliate"
    static final String OA_APPROVE1 = "approve1"
    
    static final String OA_REJECT = "reject"
    static final String OA_EXECUTE = "execute"
    static final String OA_QUERY = "query"
    static final String OA_UPDATE_OTHER = "updateOther"
    static final String OA_APPROVE_PARTIAL_APPROVED = "approvePartialApproved"
    static final String OA_PARTIALAPPROVE_PARTIAL_APPROVED = "partialApprovePartialApproved"
    static final String OA_UPDATE_QUERIED = "updateQueried"
    static final String OA_UPDATE_APPROVED = "updateApproved"
    static final String OA_UPDATE_EXECUTED = "updateExecuted"
    static final String OP_ADD_EXECUTION ="addExecution"
    
    
    static final String OA_CANCEL = "cancel"
    static final String OA_CANCEL_QUERIED = "cancelQueried"
    static final String OA_CANCEL_APPROVED = "cancelApproved"
    static final String OA_REQUEST_FROM_NEW = "requestNew"
   
    static final String OA_REQUEST_QUERIED = "requestQueried"
    static final String OA_REJECT_PARTIAL_APPROVED = "rejectPartialApproved"
    static final String OA_QUERY_PARTIAL_APPROVED = "queryPartialApproved"
    static final String OA_EDIT = "edit"
    
    
   // static final String OC_EDIT_STORED = "EditStored"
   // static final String OC_EDIT_ASSESSED = "EditAssessed"
    static final String OC_EDIT_REQUESTED = "EditRequested"
    static final String OC_EDIT_QUERIED = "EditQueried"
    static final String OC_EDIT_REJECTED = "EditRejected"
    static final String OC_EDIT_APPROVED = "EditApproved"
    static final String OC_EDIT_PARTIAL_APPROVED = "EditPartialApproved"
    static final String OC_EDIT_CANCELED = "EditCanceled"
    static final String OC_EDIT_UPDATED = "EditUpdated"
    
    static final String OC_PRINT_RELEASE_OREDER = "Print Release Order"
    static final String OC_PRINT_RELEASE_OREDER_SEL ="Release Order (selectivity)"

    static final String OP_REGISTER = "Register"
    static final String OP_CANCEL_VALID ="CancelValid"
    static final String OP_ACTIVATE ="Activate"
    static final String OP_UPDATE_VALID ="UpdateValid"
    static final String OP_VIEW_VALID = "ViewValid"
    static final String OP_VIEW_INVALID = "ViewInvalid"
    static final String OC_EDIT_INVALID = "EditInvalid"
    static final String OC_CANCEL_VALID = "CancelValid"
    static final String OA_SHOW_CANCEL = "showCancel"
    static final String OC_EDIT_VALID = "EditValid"

}


 