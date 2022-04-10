let
    pinned = builtins.fetchTarball {
        name = "nixos-21.05-2021-07-01";
        url = "https://github.com/nixos/nixpkgs/archive/1a1499ff6478821916ba64cdcf325446be39908b.tar.gz";
        # Hash obtained using `nix-prefetch-url --unpack <url>`",
        sha256 = "070dyf5gll30p608rviz1p10i4kxdamj7fq1cs1qd170ina4khm0";
    };
    nix = (import pinned) {};
in
    with nix.pkgs;
    mkShell {
        buildInputs = [ kotlin gradle jdk ];
    }
