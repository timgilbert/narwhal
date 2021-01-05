defmodule Unicorn.Timeline do
  alias Unicorn.Step

  @type t :: %__MODULE__{items: [Step.t()], repeat?: boolean()}
  defstruct items: [], repeat?: false

  @spec new(boolean()) :: t()
  def new(repeat? \\ false) do
    %__MODULE__{items: [], repeat?: repeat?}
  end

  @spec append(t(), Step.t()) :: t()
  def append(timeline, step) do
    # TODO: keep running tally of total steps?
    %__MODULE__{ timeline | items: Enum.concat(timeline.items, [step])}
  end

  def nth(timeline, pos) do
    # TODO, error handling via guards

    res = Enum.flat_map_reduce(timeline.items, nth_initial(pos), &nth_step/2)
    res
  end

  defp nth_initial(pos), do: %{pos: pos}

  @spec nth_step(any, Step.t()) :: any
  def nth_step(acc, step) do
    if acc[:pos] > step.repeat, do: %{acc | pos: acc[:pos] + step.repeat}
  end
end
