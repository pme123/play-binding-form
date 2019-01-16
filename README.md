# play-binding-form

A Single Page Application SPA that demos how are things done with this stack:
* Server: Scala / Play
* Shared: Scala
* Client: ScalaJS / Binding.scala

# Work is in progress and there are lots of loose ends!

# Web Client
* Run the client:

  `sbt run`
* There are 3 Users (password anything):
  * demoAdmin
  * demoCustomer
  * demoManager  
# Postgres
You need Docker running. Then run:

    docker run --rm -P -p 127.0.0.1:5432:5432 -e POSTGRES_PASSWORD="3sf2reRer" --name postgres -d postgres
 
And here is how you connect:   
    
    psql postgresql://postgres:3sf2reRer@localhost:5432/postgres

Or with Doobie:
    
      protected val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost/postgres",
        "postgres",
        "3sf2reRer"
      )
    
# Business View
Having a service for generic Forms, that contain of:
* Form-Elements
* Data-Structure
* Mapping between Form-Elements and Data-Structure  

# State of Work
* There are 4 main views:
  * Form Editor
  * Form Preview
  * Data Editor
  * Mapping Editor
* Forms, Data and Mappings are imported from Excel and can be selected in the according Views.
* Forms, Data and Mappings can be persisted in a DB.
  
## Form Editor
* Define HTML-Elements
* Supported are some examples Elements (text, text-area, dropdown, title ...)
* Some example Validation
* Entries for Dropdowns
* Move Element with Drag and Drop
* Move Dropdown Entries with Drag and Drop
* Translations for Label, Placeholder and Tooltip
* Add, copy and delete an Element
* Supports simple Layout (Semantic - Grid)

## Form Preview
* Show the defined Elements in the Grid Layout
* Try it out
* Validate the form
* Export the Elements as JSON
* Save the Elements in the DB

## Data Editor
* Create the Data-structure
* Move Element with Drag and Drop
* Add and delete an Element
* Create a Data-Structure from a Form
* Export the Data-Structure as JSON
* Save the Data-Structure in the DB
* Import a Data-Structure JSON

## Mapping Editor
* Auto create the Mapping from Form-Elements and Data-Structure
* Select possible Mappings from Data-Structure
* Displays the Form, like in the Preview.
* Validates the Mapping

# Technical View
see the `Settings` file for all dependencies!
## Client
* `com.thoughtworks.binding.Route` for the routing.
* `com.thoughtworks.binding.Binding` for handling the state.
* `com.thoughtworks.binding.FutureBinding` for asynchronous Server calls.
* `play-jsmessages` for i18n Messages

## Shared
* `play-json` for serialization

## Server
* `play` as web framework
* `silhouette` for authentication
* `doobie` for database access
