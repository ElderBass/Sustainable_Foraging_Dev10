# Sustainable Foraging Plan

## Overview
* Spring DI first and foremost
* Debug existing features
  * Also ensure they are meeting application requirements
* Add new features
  * Test new methods while I go
  
## Spring DI
* Pretty simple process of setting up required components in the XML file
  * I may switch this to annotation form since the instructors suggested that method
* Basically, for XML, just initialize all the needed repositories, services, view, and finally the Controller
  * Then set up the Application Context in App and call Controller.run() to run app
  
## Debugging and Verification
Features that Need Testing:
  1. Add an Item.
  2. View Items.
  3. View Foragers.
  4. Add a Forage.
  5. View Forages by date.
* Start by running the app, going feature by feature, trying to break the app and noting when I do
  * Take notes as I go of what actions led to crashes
  * Also note if things are clunky and could be improved from a UI/UX perspective
* Need to test out all edge cases to see if app is handling them well
  * Make note of when I can input faulty data or otherwise slip passed validation walls
* Review notes then scour the code to track where the bugs are occurring
  * Make note of which methods need tuning, and/or if some methods need to be added 
  
## Adding New Features
Three Features that Need to be Added:
  1. Add a Forager
  2. Create a report that displays the kilograms of each Item collected on one day.
  3. Create a report that displays the total value of each Category collected on one day.

### Add a Forager
* Should be a pretty straightforward CRUD route
  * Start in ForagerFileRepository with an addForager() method
      * Test this method once complete to ensure it's working as anticipated
      * Method should add Forager to the list of Foragers and write the Forager to the data file
      * This may be tied to ForageFileRepository, not sure yet need to investigate that
  * Need to identify all the fields that are required for a Forager class and their validity ranges/rules
      * Implement this verification in the ForagerService file
      * have an addForager() method in ForagerService to make sure the Forager is meeting all requirements for the class
      * This will involve writing a validate() method inside ForagerService
      * Test this method before continuing to ensure it's working properly
  * Move from domain to UI - add the createForager() method in Controller, adding it to main menu switch statement
    * Also add a createForager() method inside View, prompting user for required fields to make a new Forager
    * Make sure this createForager() is part of the main menu as well
  * Test out addForager by running the app and ensuring that validation walls are secure, and foragers are successfully written to the data file
  
### Report for Kilograms for each Item on a given day
**Need to figure out if I'm thinking about this correctly**
* Given a date as entered by the user, generate a report displaying the kilograms/item for all items on that date's forage
* Make the query to the data file in the ForageFileRepository
  * i.e. that is where this feature will start, with an aptly named method inside ForageFileRepo
* This will need to utilize a stream
  * Use the findByDate() method in ForageFileRepo to get a List of all the forages on that date
  * For each of those forages we find, display the Item name and its kilograms in a line
    * Should be a straightforward query, something like forages.stream.forEach(forage -> System.out("Item: " + forage.getItem.getName + ", Kg: " forage.getKilograms)
  
### Report for Total Value of all Items in each Category
**LMS says to do this with loops but, at least on the surface, this seems doable with Streams so I might do that instead**
* Not sure how to execute this yet but basically need to query a Forage (on a certain day or...?) to get a list of all the items
  * For all the Items in the list, we make note of its Category, kilograms, and dollarsPerKilogram
    * So for each forage, multiply the number of kilos of that Item we foraged by its dollars/kilo field
    * Will have to use BigDecimal types to make this happen since dollars/kilo field is a BigDecimal
    * Add this total to a "sum" variable for its respective Category
    * Do this for all Items in the forage, summing up their values in all Category sum variables
  * Once we have totals for each category, print them to console in some logical/presentable fashion
* Again this method will likely start from ForageFileRepository and move up through the layers
  * Need to test this somehow in Repository and Service
  * I'm not sure what this will look like in View/Controller, as in I'm not sure if a user will pick a certain date again to generate this report
  
  

## Tasks
* Set up Spring and inject dependencies into framework, set up controller in App
  * Estimated Time: 20 minutes
* Check for bugs in the features listed on the LMS and fix them
    * Make sure all the requirements are being yet, e.g. validation for fields
    * Estimated Time: 3-5 hours - hard to tell, depends on how many bugs there are
* Add route for adding Foragers
  * Multi-Step task
    * Add method in ForagerFileRepo
      * Tests for this method
    * Add method in ForagerService
      * Tests for this as well
    * Add method in Controller and complement in View
      * Also need to add the "Add" feature to the main menu for users
  * Estimated Time: 4 hours

* Create a report that displays the kilograms of each Item collected on one day.
  * I'll have to look at how the reports are set up to figure out what kind of methods I need
  * Basically this will involve a stream query of a specific LocalDate as entered by the user to display the kilograms of each item collected in a day
    * But yeah, uses findByDate, create a stream from it, then print out Items and their respective Kilos
  * Create this as a menu option in View/Controller
  * Estimated Time: 2-3 hours
  
* Create a report that displays the total value of each Category collected on one day.
  * Again, fairly straightforward feature
  * This will start in the ForageFileRepository and move on up
  * Not sure if I need to findByDate but it seems likely
  * Estimated Time: 2-3 hours
  