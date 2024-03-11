{
  description = "Java development environment";
  inputs.flake-utils.url = "github:numtide/flake-utils";
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  outputs = { self, flake-utils, nixpkgs }:
    flake-utils.lib.eachDefaultSystem (system:
      let pkgs = nixpkgs.legacyPackages.${system}; in
      {
        devShells.default = with pkgs; mkShell {
          packages = [
            # Gradle, Java development kit, and Java language server.
            bash
            coreutils
            gradle
            jdk
          ];
        };
      }
    );
}
