Updated: Feb 2021

### Introduction

The point of `fabric-envoy` (one of many ApplicationModules) is to model JPMC's:
- <b>DIP (Data in Place)</b> - the data stored in all databases
- <b>DIM (Data in Motion)</b> - the data exposed via APIs, Events, and Files

ApplicationModules uses DIP, and exposes it via DIM to other ApplicationModules

# Knowledge Graph
To model DIP, DIM, and the relationships between ApplicationModules we use Neo4j to store its metadata. <b>NOTE: we do NOT store the raw data, but the metadata</b>. For example:
- for DIP we store things like: table names, column names, column data types, etc
- for DIM we store things like: API's response and request models, etc

### ER Diagram of Knowledge Graph
below is an ER Diagram of our Knowledge Graph 
```mermaid
erDiagram
    Application ||--o{ LogicalDataModel : "IS_OWNED_BY"
    ApplicationModule }o--o{ LogicalDataModel : "USES"
    LogicalDataModel ||--|| PhysicalModel : "IS_OWNED_BY"
    PhysicalModel ||--o{ PhysicalTable : "IS_OWNED_BY"
    PhysicalTable ||--o{ PhysicalColumn : "IS_OWNED_BY"
    Application ||--o{ ApplicationModule : "IS_OWNED_BY"
    ApplicationModule ||--o{ Api : "PROVIDES"
    ApplicationModule }o--|| Api : "CONSUMES"
    ApplicationModule }o--o{ Event : "PUBLISHES"
    ApplicationModule }o--|| Event : "SUBSCRIBES"
    Api ||--o| Response : "IS_OWNED_BY"
    Api ||--o| Request : "IS_OWNED_BY"
    Event ||--o| Model : "IS_OWNED_BY"
```
The DIP metadata are modeled with entities falling under `LogicalDataModel`:
- `PhysicalModel`
- `PhysicalTable`
- `PhysicalColumn`

The DIM metadata are modeled with entities falling under `ApplicationModule`:
- `Api`
- `Event`
- `Response`
- `Request`
- `Model`

Since we are using an Object-Oriented Language, each of these entities have a one-to-one correspondence with a class under the `model` package

### Class Diagram of Knowledge Graph
```mermaid
classDiagram
    Asset <|-- OwnableAsset
    OwnableAsset <|-- Application
    OwnableAsset <|-- ApplicationModule
    OwnableAsset <|-- LogicalDataStore
    OwnableAsset <|-- PhysicalModel
    OwnableAsset <|-- PhysicalTable
    OwnableAsset <|-- PhysicalColumn
    Asset <|-- Api
    Asset <|-- Event
    Asset <|-- Model
    Model <|-- Request
    Model <|-- Response
    class Asset {
        +String uuid
    }
    class OwnableAsset~Owner extends Asset~ {
        +owner
    }
    class Application {
        +sealId
        +List~ApplicationModule~
        +List~LogicalDataStore~
    }
    class ApplicationModule {
        +sealModuleId
        +List~LogicalDataStore~ ldsUsed
        +List~Api~ apisProvided
        +List~Api~ apisConsumed
        +List~Event~ eventsPublished
        +List~Event~ eventsSubscribed    
    }
    class LogicalDataStore~Application~ {
        +PhysicalModel
    }
    class PhysicalModel~LogicalDataStore~ {
        +List~PhysicalTable~
    }
    class PhysicalTable~PhysicalColumn~ {
        +List~PhysicalColumn~
    }
    class PhysicalColumn~PhysicalTable~ {
        column metadata here
    }
    class Api {
        +List~ApplicationModule~ consumedBy
        +ApplicationModule providedBy
        +Response
        +Request
    }
    class Event {
        +List~ApplicationModule~ subscribedBy
        +List~ApplicationModule~ publishedBy
        +Model
    }
    class Model {
        +schema
    }
    class Request~Api~ {
    }
    class Response~Api~ {
    }
```