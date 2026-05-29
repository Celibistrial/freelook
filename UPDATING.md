# Updating Minecraft / Fabric Versions

Reference: https://fabricmc.net/versions.html and https://meta.fabricmc.net

## Steps

1. Check target Minecraft version is stable:
   ```
   https://meta.fabricmc.net/v2/versions/game
   ```

2. Get latest loader version:
   ```
   https://meta.fabricmc.net/v2/versions/loader/<minecraft_version>
   ```

3. Get latest Fabric API version for that Minecraft version (check Modrinth):
   ```
   https://api.modrinth.com/v2/project/fabric-api/version?game_versions=["<minecraft_version>"]&loaders=["fabric"]
   ```
   Use the most recent `version_number` value.

4. Update `gradle.properties`:
   ```properties
   minecraft_version=<new_version>
   loader_version=<new_loader_version>
   fabric_version=<new_fabric_api_version>
   mod_version=<bump_patch>
   ```

5. Commit and push.

## Example (26.1.1 → 26.1.2)

```properties
minecraft_version=26.1.2
loader_version=0.19.2
fabric_version=0.150.0+26.1.2
mod_version=1.4.1
```
