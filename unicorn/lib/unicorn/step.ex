defmodule Unicorn.Step do
  defstruct effect: nil, duration_ms: 0, repeat: 1

  @spec new(any, non_neg_integer(), non_neg_integer()) :: %__MODULE__{}
  def new(effect, duration_ms, repeat) do
    %__MODULE__{effect: effect, duration_ms: duration_ms, repeat: repeat}
  end
end
