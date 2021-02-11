defmodule NarwhalUiWeb.Resolvers.Effect do
  alias Unicorn.Effect
  require Logger

  def effect_type_resolver(effect, _args, _resolution) do
    case effect do
      #      %Effect.Replace{} -> {:ok, :replace}
      #      %Effect.Tween{} -> {:ok, :tween}
      _ -> {:error, "What the?!"}
    end
  end

  def pause_ms_resolver(effect, _args, _resolution) do
    {:ok, Effect.Effect.pause_ms(effect)}
  end

  def duration_ms_resolver(effect, _args, _resolution) do
    {:ok, Effect.Effect.duration_ms(effect)}
  end

  def granularity_resolver(effect, _args, _resolution) do
    {:ok, Effect.Effect.granularity(effect)}
  end

end
