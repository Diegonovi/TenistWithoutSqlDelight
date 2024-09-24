package org.example.tenist.errors

sealed class TenistError (val msg : String){
    class InvalidTenist(msg : String) : TenistError(msg)
    class TenistAlreadyExists(msg : String) : TenistError(msg)
    class TenistDoesNotExist(msg : String) : TenistError(msg)
    class ImportError(msg : String) : TenistError(msg)
    class ExportError(msg : String) : TenistError(msg)
    
}