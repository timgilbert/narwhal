defmodule Unicorn.Timeline do
  alias Unicorn.Step

  @type t :: %__MODULE__{items: [Step.t()], repeat?: boolean(), total: non_neg_integer}
  defstruct items: [], repeat?: false, total: 0

  @spec new(boolean()) :: t()
  def new(repeat? \\ false) do
    %__MODULE__{items: [], repeat?: repeat?, total: 0}
  end

  @spec append(t(), Step.t()) :: t()
  def append(timeline, step) do
    %__MODULE__{
      timeline
      | items: Enum.concat(timeline.items, [step]),
        total: timeline.total + step.repeat
    }
  end

  @spec nth(t(), non_neg_integer) :: {:ok, Step.t()} | {:err, String.t()}
  def nth(timeline, pos) do
    # TODO, error handling via guards
    if pos >= timeline.total and not timeline.repeat? do
      {:err, "Tried to get element #{pos} out of #{timeline.total} in non-repeating timeline!"}
    else
      IO.inspect(timeline.items, label: "timeline.items")
      target = rem(pos, timeline.total)
      |> IO.inspect(label: "target")

      timeline.items
      |> Enum.reduce_while(0, fn step, i ->
        next = i + step.repeat

        IO.inspect(step, label: "step")
        IO.inspect("=================")
        IO.inspect(i, label: "i")
        IO.inspect(target, label: "target")
        IO.inspect(next, label: "next")
        IO.inspect(timeline.total, label: "timeline.total")
        IO.inspect(step.repeat, label: "step.repeat")
        IO.inspect(".................")
        IO.inspect(i < target, label: "i < target")
        IO.inspect(i > timeline.total, label: "i > timeline.total")
        #    IO.inspect(pos >= step.repeat, label: "pos >= step.repeat")
        IO.inspect("-----------------")

        cond do
          next <= target ->
            {:cont, next}

          i > timeline.total ->
            {:halt, {:err, "Fell off end"}}

          true ->
            {:halt, {:ok, step}}
        end
        |> IO.inspect(label: "res")
      end)
    end
    |> IO.inspect(label: "nth result")
  end

#  defp nth_initial(timeline, target) do
#  end
#
#  @spec nth_step(Step.t(), any) :: any
#  def nth_step(step, acc) do
#    %{pos: pos, total: total, target: target, repeat?: repeat?} = acc
#    next = pos + step.repeat
#
#    IO.inspect(step, label: "step")
#    IO.inspect(acc, label: "acc")
#    IO.inspect("=================")
#    IO.inspect(pos, label: "pos")
#    IO.inspect(target, label: "target")
#    IO.inspect(next, label: "next")
#    IO.inspect(total, label: "total")
#    IO.inspect(step.repeat, label: "step.repeat")
#    IO.inspect(".................")
#    IO.inspect(pos < target, label: "pos < target")
#    IO.inspect(pos > total, label: "pos > total")
#    #    IO.inspect(pos >= step.repeat, label: "pos >= step.repeat")
#    IO.inspect("-----------------")
#
#    cond do
#      pos < target ->
#        {:cont, %{acc | pos: next}}
#
#      pos > total ->
#        if repeat? do
#          {:cont, %{acc | pos: next - total}}
#        else
#          {:halt, {:err, "Fell off end"}}
#        end
#
#      true ->
#        {:halt, {:ok, step}}
#    end
#    |> IO.inspect(label: "res")
#  end

end
