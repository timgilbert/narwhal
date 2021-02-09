defmodule Unicorn.Step do
  alias Unicorn.Effect

  @type t :: %__MODULE__{effects: [Effect.t()], pause_ms: non_neg_integer,
                         repeat: pos_integer}
  defstruct effects: [], pause_ms: 0, repeat: 1

  @spec new([Effect.t()], non_neg_integer(), non_neg_integer()) :: %__MODULE__{}
  def new(effects, pause_ms, repeat) do
    %__MODULE__{effects: effects, pause_ms: pause_ms, repeat: repeat}
  end

  def new(%{effects: bare_effects, pause_ms: pause_ms, repeat: repeat}) do
    effects = Enum.map(bare_effects, &Effect.new/1)
    %__MODULE__{effects: effects, pause_ms: pause_ms, repeat: repeat}
  end
end
