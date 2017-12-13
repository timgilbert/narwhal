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
  init: [:nerves_runtime, :nerves_network],
  app: Mix.Project.config[:app]

# https://hexdocs.pm/nerves/user-interfaces.html#configure-networking
# For WiFi, set regulatory domain to avoid restrictive default
config :nerves_network,
  regulatory_domain: "US"

# https://github.com/nerves-project/nerves_network#wired-networking
config :nerves_network, :default,
  eth0: [
    ipv4_address_method: :dhcp
    #ipv4_address: "10.0.0.30", ipv4_subnet_mask: "255.255.255.0",
    #nameservers: ["8.8.8.8", "8.8.4.4"]
  ]

config :ui, UiWeb.Endpoint,
  url: [host: "10.0.0.30"],
  http: [port: 80],
  secret_key_base: "dlwoBdIMqgwdo1gKNcOxyAmKZRnV/rJlKGyPNs4/bU2LdAGTC+n3ieLlSypZXFGj",
  root: Path.dirname(__DIR__),
  server: true,
  render_errors: [view: UiWeb.ErrorView, accepts: ~w(html json)],
  pubsub: [name: Nerves.PubSub, adapter: Phoenix.PubSub.PG2],
  code_reloader: false

config :logger, level: :debug

# Import target specific config. This must remain at the bottom
# of this file so it overrides the configuration defined above.
# Uncomment to use target specific configurations

# import_config "#{Mix.Project.config[:target]}.exs"
