{
  description = "Java development environment";
  inputs.flake-utils.url = "github:numtide/flake-utils";
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  outputs = {
    self,
    flake-utils,
    nixpkgs,
  }:
    flake-utils.lib.eachDefaultSystem (
      system: let
        pkgs = nixpkgs.legacyPackages.${system};
        libs = with pkgs; [
          libpulseaudio
          libGL
          glfw
          openal
          stdenv.cc.cc.lib
        ];
      in {
        devShells.default = with pkgs;
          mkShell {
            buildInputs = libs;
            LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath libs;
            packages = [
              # Gradle, Java development kit, and Java language server.
              bash
              coreutils
              gradle
              jdk21
            ];
          };
      }
    );
}
