# Set Homes Two

### Introduction
This plugin allows players to set multiple homes across the various worlds and teleport to with ease. 
Additionally, server admins have the ability to blacklist certain dimensions, restricting the ability to set a home in those dimensions. 
If a home has already been created in a dimension and that dimension gets blacklisted later, the player will still see the home in their inventory, but will not be able to teleport to it.
Using the config you can control setting such as a maximum number of homes, teleport cool down, teleport delay, and their respective messages that get display to the user.

### Installation
Simply place the downloaded jar into your server plugins folder edit the config.yml file to get set-homes set up.
An example setup can be found before the screenshots section.

**NOTE**: For `maxHomes` to work you must install the soft dependency below, and setup groups for the respective permissions plugin.

### Soft Dependencies
- [LuckPerms](https://luckperms.net/download)

### Commands
- `/create-home [name] [display_material | d | default] [description]` - Will create a home where the player is standing with the given name, material chosen, and description. 
- `/delete-home [name]` - Will delete the home with the name provided.
- `/add-to-blacklist [dimension names]` - This will add the specified dimension to the blacklisted table. If a dimension is present in this table, players will not be able to have save their homes in that dimension.
- `/remove-from-blacklist [dimension names]` - This will remove the specified dimension from the blacklisted table.
- `/get-blacklisted-dimensions` - This will return a list of the dimensions that are in the blacklisted table
- `/set-max-homes [group name] [number]` - This will update the maximum number of homes that players are allowed to set. 
If the plugin is set up to allow different groupings or tiers for players, you will need to provide a group name in addition to the number of max homes. 
If the plugin is set up to only have one group or tier, you only need to provide the number of max homes.

### Permissions

- `sh2.give-homes-item` - Allow player to get homes viewing/teleportation item.
- `sh2.create-home` - Allow player to create homes.
- `sh2.delete-home` - Allow player to delete their own homes.
- `sh2.add-to-blacklist` - Add dimensions to blacklist table.
- `sh2.remove-from-blacklist` - Remove dimensions from blacklist table.
- `sh2.get-blacklist-dimensions` - Retrieve a list of the blacklisted dimensions.
- `sh2.get-player-homes` - Retrieve a list of a given player's homes.
- `sh2.set-max-homes` - Set the max number of homes all players, or individual groups, can have.

### Extra Features
- The time it takes to teleport to a saved home can be configured
- Players can choose which Minecraft item they would like to use as an icon for their saved homes (each home can use a different Minecraft item)
- Server admins can configure what the default item is for saved home's icon

### Example Config
```yaml
# -- HOMES --
# The item the player will use when
# right-clicking to open the homes list.
openHomeItem: "compass"

# The default item when create-home command
# is only given a home name, or is only
# provided with the default material name.
defaultHomeItem: "white_wool"

# The title of the inventory displaying the
# players home list.
inventoryTitle: "Your homes"

# Enabled maximum number of homes.
maxHomeEnabled: false

# The maximum number of homes setup type.
# Choices are: singular | groups
# Example of singular maxHomesType:
# maxHomesType: "singular"
# maxHomes: 5
maxHomesType: groups
maxHomes:
  admin: 5
  user: 4

# -- TELEPORTING --
teleportTitle: "Please stand still"
teleportSubtitle: "You will be teleported in %d..." # You can use %d here as a placeholder for the seconds counter.
teleportSuccess: "Teleported to %s" # You can use %s here as a placeholder for the home name the player was teleported to.
cancelOnMove: true # true | false
delay: 3 # (seconds) 0 is no delay.

# -- MESSAGES --
homeCreated: "%s has been created successfully." # You can use %s here as a placeholder for the players home name.
homeDeleted: "%s has been deleted successfully." # You can use %s here as a placeholder for the players home name.
dimensionAddedToBlacklist: "%s has been added to the blacklist." # You can use %s here as a placeholder for the dimension names.

# -- ERROR MESSAGES --
invalidHomeItem: "The material you entered is not valid, please try a different one."
falseHomeItem: "This home item does not belong to you."
teleportedWhileTeleporting: "You cannot teleport while already teleporting."
movedWhileTeleporting: "Teleport has been cancelled because you have moved."
noHomes: "You have not created any homes yet. Use /create-home to make your first one."
teleportToBlacklistedDimension: "You cannot teleport to this home because the dimension is blacklisted."
maxHomesReached: "You have reached the maximum number of homes allowed."
dimensionBlacklisted: "You cannot set home in this dimension because it is blacklisted."

# -- DEBUGGING --
debugLevel: "error" # Choices are: error | info
```
### Donations
Please feel free to donate via the button below, any amount is greatly appreciated. Donating will help keep this plugin up to date. Thank you!

[![Donate](./src/main/img/donateBtn.png)](https://www.paypal.com/donate/?return=https://dev.bukkit.org/projects/312833&cn=Add+special+instructions+to+the+addon+author()&business=sam%40samleighton.us&bn=PP-DonationsBF:btn_donateCC_LG.gif:NonHosted&cancel_return=https://dev.bukkit.org/projects/312833&lc=US&item_name=Set+Homes+(from+bukkit.org)&cmd=_donations&rm=1&no_shipping=1&currency_code=USD)

### Screenshots
![Screenshot](https://imgur.com/ucK48vf.png)
![Screenshot](https://imgur.com/xoifjIv.png)
![Screenshot](https://imgur.com/TnqcR9i.png)
![Screenshot](https://imgur.com/pR3qJ2Q.png)

### F.A.Q
- **Q: How can I give players permission to set named homes?**
  **A:** You will need to install the permission plugin, [LuckPerms](https://luckperms.net/download) then configure the config.yml to allow for multiple groups (see above for example config).

### Changelog
