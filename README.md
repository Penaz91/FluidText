# FluidText
Small Parsing plugin to create JSON-Based chat helpfiles for Bukkit/Spigot 1.8
## Helpfile Syntax
All helpfiles are simple TextFiles using the legacy ampersand notation (&1...&f) with the addition of 2 more items that will allow to customize the helpfiles
**{PLAYER}** Will be replaced with the player name that executes the command
**´Text|Click_Action|Hover_Action|Click_Value|Hover_Value´** To create a button
##Creating a button
Creating a button is simple, here are some examples:
###Simple button
**´[Click Me]|run_command||/say Hello|´**

This will create a button that will have [Click Me] as text and will run the command "/say Hello" when clicked.
###Simple Button With Hover Option
**´[Click Me]|run_command|show_text|/say Hello|Click me´**

Same as before, but upon hovering a small "Click me" tooltip will appear.
###Button with Item Tooltip and command suggestion function
**´[Click Me]|suggest_command|show_item|/msg Owner|1´**

This will create a button which will have [Click Me] as test, will suggest the command (type it in the chatbox) "/msg Owner" upon clicking and will show the Information about the item with ID "1" in the Items.yml file.
##In-game Commands

* **/colors** Will show the command sender an example of all the formats available on Bukkit (except &k)
* **/fluidhelp file** Parses file.txt and displays it in chat
* **/fluidyaml file** Parses file.yml and disaplys it in chat (useful for expanding rules with more information)
* **/setitem id** Replaces the item with Identifier "id" with the item you're currently holding
* **/delitem id** Deletes the item with Identifier "id"
* **/getitem id** Spawns the item with Identifier "id", so it can be modified.
