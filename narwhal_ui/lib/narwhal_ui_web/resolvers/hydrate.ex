defmodule NarwhalUiWeb.Resolvers.Hydrate do
  alias Unicorn.Effect
  alias Unicorn.Target
  alias Unicorn.Step
  alias Unicorn.Timeline
  require Logger

  def hydrate_effect(%{type: :replace_effect, target: target} = args) do
    case hydrate_frame_target(target) do
      {:ok, hydrated} ->
        case args do
          %{pause_ms: pause_ms} ->
            {
              :ok,
              Effect.Replace.new(
                hydrated,
                pause_ms
              )
            }
          _ ->
            {:error, "hydrate_effect: not enough data in " <> inspect args}
        end
      err -> err
    end
  end

  def hydrate_effect(%{type: :tween_effect, target: target} = args) do
    case hydrate_frame_target(target) do
      {:ok, hydrated} ->
        case args do
          %{
            pause_ms: pause_ms,
            duration_ms: duration_ms,
            granularity: granularity
          } ->
            {
              :ok,
              Effect.Tween.new(
                hydrated,
                pause_ms,
                duration_ms,
                granularity
              )
            }
          _ ->
            {:error, "hydrate_effect: not enough data in " <> inspect args}
        end
      err -> err
    end
  end
  def hydrate_effect(%{type: unknown}) do
    {:error, "hydrate_effect: don't understand effect type :#{unknown}"}
  end
  def hydrate_effect(args) do
    {:error, "hydrate_effect: no type argument found in " <> inspect args}
  end

  def hydrate_frame_target(%{type: :random_frame}) do
    {:ok, Target.Random.new()}
  end
  def hydrate_frame_target(%{type: :solid_frame} = args) do
    case args do
      %{color: color} -> {:ok, Target.Solid.new(color)}
      _ -> {:error, "No color specified in " <> inspect args}
    end
  end
  def hydrate_frame_target(%{type: :saved_frame} = args) do
    case args do
      %{frame_id: frame_id} -> {:ok, Target.Saved.new(frame_id)}
      _ -> {:error, "No frame_id specified in " <> inspect args}
    end
  end
  def hydrate_frame_target(%{type: unknown} = args) do
    Logger.warn(inspect args)
    {
      :error,
      "hydrate_frame_target: don't understand target type :#{unknown}! "
      <> inspect args
    }
  end
  def hydrate_frame_target(args) do
    {:error, "hydrate_frame_target: no type argument specified in " <> inspect args}
  end

  def hydrate_step(
        %{
          effects: effects,
          pause_ms: pause_ms,
          repetitions: repetitions
        }
      ) do
    hydrated_effects =
      effects
      |> Enum.map(&hydrate_effect/1)
      |> Enum.reduce_while(
           [],
           fn e, acc ->
             case e do
               {:ok, eff} -> {:cont, acc ++ [eff]}
               {:error, msg} -> {:halt, {:error, msg}}
             end
           end
         )
    case hydrated_effects do
      {:error, _} = res -> res
      es -> {:ok, Step.new(es, pause_ms, repetitions)}
    end
  end
  def hydrate_step(args) do
    {:error, "hydrate_step: not enough info in " <> inspect args}
  end

  def hydrate_timeline(%{is_repeat: repeat?, steps: steps}) do
    timeline = Timeline.new(repeat?)
    Enum.reduce_while(
      steps,
      timeline,
      fn step, t ->
        case hydrate_step(step) do
          {:ok, hydrated} -> {:cont, Timeline.append(t, hydrated)}
          err -> {:halt, err}
        end
      end
    )
  end
  def hydrate_timeline(args) do
    {:error, "hydrate_timeline: not enough info in " <> inspect args}
  end

end
