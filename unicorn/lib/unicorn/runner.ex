defmodule Unicorn.Runner do
  use GenServer

  alias Unicorn.Timeline
  alias Unicorn.Step
  alias Unicorn.Frame

  @buffer_size 10

  @type t :: %__MODULE__{timeline: Timeline.t(), counter: non_neg_integer, buffer: [Frame.t()]}
  defstruct timeline: nil, counter: 0, buffer: []

  @impl true
  def init(:ok) do
    {:ok, %__MODULE__{}}
  end

  @impl true
  def handle_call({:timeline, timeline}, _from, state) do
    new_state = %__MODULE__{state | timeline: timeline, counter: 0}
    # TODO: generate buffer
    {:replay, :ok, new_state}
  end

  def next(runner) do
    # TODO, error handling via guards
    {:ok, step} = Timeline.nth(runner.timeline, runner.counter)
    {:ok, %__MODULE__{ runner | counter: runner.counter + 1}, step}

  end
end
