 # This file is responsible for configuring your application
# and its dependencies with the aid of the Mix.Config module.
#
# This configuration file is loaded before any dependency and
# is restricted to this project.
use Mix.Config

# Customize the firmware. Uncomment all or parts of the following
# to add files to the root filesystem or modify the firmware
# archive.

# config :nerves, :firmware,
#   rootfs_overlay: "rootfs_overlay",
#   fwup_conf: "config/fwup.conf"

# Use bootloader to start the main application. See the bootloader
# docs for separating out critical OTP applications such as those
# involved with firmware updates.
config :bootloader,
  init: [:nerves_runtime, :nerves_init_gadget],
  app: Mix.Project.config[:app]

# https://hexdocs.pm/nerves/user-interfaces.html#configure-networking
# For WiFi, set regulatory domain to avoid restrictive default
config :nerves_network,
  regulatory_domain: "US"

# https://github.com/nerves-project/nerves_network#wired-networking
# https://github.com/nerves-project/nerves_examples/tree/master/hello_network#how-to-use-the-wifi-interface
key_mgmt = System.get_env("NERVES_NETWORK_KEY_MGMT") || "WPA-PSK"
config :nerves_network, :default,
  wlan0: [
    ssid: System.get_env("NERVES_NETWORK_SSID"),
    psk: System.get_env("NERVES_NETWORK_PSK"),
    key_mgmt: String.to_atom(key_mgmt)
  ],
  eth0: [
    ipv4_address_method: :dhcp
    #ipv4_address: "10.0.0.30", ipv4_subnet_mask: "255.255.255.0",
    #nameservers: ["8.8.8.8", "8.8.4.4"]
  ]

config :nerves_firmware_ssh,
  authorized_keys: [
    File.read!(Path.join(System.user_home!, ".ssh/id_rsa.pub"))
  ]

config :nerves_init_gadget,
  ifname: "wlan0",
  address_method: :dhcp,
  mdns_domain: "narwhal.local",
  node_name: "narwhal",
  node_host: :mdns_domain

config :ui, UiWeb.Endpoint,
  url: [host: "10.0.0.30"],
  http: [port: 80],
  secret_key_base: "dlwoBdIMqgwdo1gKNcOxyAmKZRnV/rJlKGyPNs4/bU2LdAGTC+n3ieLlSypZXFGj",
  root: Path.dirname(__DIR__),
  server: true,
  render_errors: [view: UiWeb.ErrorView, accepts: ~w(html json)],
  pubsub: [name: Nerves.PubSub, adapter: Phoenix.PubSub.PG2],
  code_reloader: false


config :logger, :logger_papertrail_backend,
  url: System.get_env("PAPERTRAIL_URL") || "papertrail://logs6.papertrailapp.com:39329/narwhal.localdev",
  level: :info,
  format: "$metadata $message"

config :logger,
  backends: [ :console,
    LoggerPapertrailBackend.Logger
  ],
  level: :debug

# Import target specific config. This must remain at the bottom
# of this file so it overrides the configuration defined above.
# Uncomment to use target specific configurations

# import_config "#{Mix.Project.config[:target]}.exs"
