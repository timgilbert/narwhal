defmodule Unicorn.Timeline do
  alias Unicorn.Step
  alias Unicorn.Fx.Tween

  @type t :: %__MODULE__{
               steps: [Step.t()],
               repeat?: boolean(),
               total: non_neg_integer
             }
  defstruct [steps: [], repeat?: false, total: 0]

  #  def new(%{steps: bare_steps, is_repeat: repeat?}) do
  #    acc = %__MODULE__{steps: [], repeat?: repeat?, total: 0}
  #    Enum.reduce(
  #      bare_steps,
  #      acc,
  #      fn step, acc ->
  #        append(acc, Step.new(step))
  #      end
  #    )
  #  end

  @spec new(boolean()) :: t()
  def new(repeat?) do
    %__MODULE__{steps: [], repeat?: repeat?, total: 0}
  end

  @spec append(t(), Step.t()) :: t()
  def append(timeline, step) do
    %__MODULE__{
      timeline
    |
      steps: Enum.concat(timeline.steps, [step]),
      total: timeline.total + step.repeat
    }
  end

  def tween_to(timeline, target_frame, steps, duration) do
    duration_each = div(duration, steps)
    scales = Enum.map(1..steps, fn i -> i / steps end)
    Enum.reduce(
      scales,
      timeline,
      fn scale, timeline ->
        step = Step.new([Tween.new(target_frame, scale)], duration_each, 1)
        append(timeline, step)
      end
    )
  end

  @spec nth(t(), non_neg_integer) :: {:ok, Step.t()} | {:err, String.t()}
  def nth(timeline, pos) do
    if pos >= timeline.total and not timeline.repeat? do
      {:err, "Tried to get element #{pos} out of #{timeline.total} in non-repeating timeline!"}
    else
      target = rem(pos, timeline.total)

      Enum.reduce_while(
        timeline.steps,
        0,
        fn step, i ->
          next = i + step.repeat

          cond do
            # If next is under target, advance
            next <= target -> {:cont, next}
            # If we've gone past the end, error
            i > timeline.total -> {:halt, {:err, "Fell off end"}}
            # Otherwise we're in the right band, return the step
            true -> {:halt, {:ok, step}}
          end
        end
      )
    end
  end
end
