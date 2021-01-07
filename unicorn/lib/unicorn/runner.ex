defmodule Unicorn.Runner do
  alias Unicorn.Timeline
  alias Unicorn.Step
  alias Unicorn.Frame

  @buffer_size 10

  @type t :: %__MODULE__{timeline: Timeline.t(), counter: non_neg_integer, buffer: [Frame.t()]}
  defstruct timeline: nil, counter: 0, buffer: []

  @spec new(Timeline.t()) :: t()
  def new(timeline) do
    %__MODULE__{timeline: timeline}
  end

  def next(runner) do
    # TODO, error handling via guards
    {:ok, step} = Timeline.nth(runner.timeline, runner.counter)
    {:ok, %__MODULE__{ runner | counter: runner.counter + 1}, step}

  end
end
