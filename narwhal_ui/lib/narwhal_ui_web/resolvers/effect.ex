defmodule NarwhalUiWeb.Resolvers.Effect do
  @moduledoc false
  alias Unicorn.Effect
  require Logger
  alias NarwhalUi.Repo

  def effect_type(effect, _args, _resolution) do
    {:ok, :replace}
  end

  def effect_duration_ms(effect, _args, _resolution) do
    {:ok, 0}
  end

  def effect_granularity(effect, _args, _resolution) do
    {:ok, 1}
  end

  def hydrate_effect(%{type: :replace} = args) do

  end
end
