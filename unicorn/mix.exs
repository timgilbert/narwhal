defmodule Unicorn.MixProject do
  use Mix.Project

  def project do
    [
      app: :unicorn,
      version: "0.1.0",
      elixir: "~> 1.11",
      start_permanent: Mix.env() == :prod,
      deps: deps()
    ]
  end

  # Run "mix help compile.app" to learn about applications.
  def application do
    [
      extra_applications: [:logger, :crypto],
      mod: {Unicorn.Application, []}
    ]
  end

  # Run "mix help deps" to learn about dependencies.
  defp deps do
    [
      {:circuits_spi, "~> 0.1"},
      {:tint, "~> 1.1"}
    ]
  end
end
