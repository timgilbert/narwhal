defmodule Unicorn.Step do
  alias Unicorn.Effect

  @type t :: %__MODULE__{
               effects: [Effect.Effect.t()],
               pause_ms: non_neg_integer,
               repetitions: pos_integer
             }
  defstruct [effects: [], pause_ms: 0, repetitions: 1]

  @spec new(
          [Effect.Effect.t()],
          non_neg_integer(),
          non_neg_integer()
        ) :: %__MODULE__{}
  def new(effects, pause_ms, repetitions) do
    %__MODULE__{
      effects: effects,
      pause_ms: pause_ms,
      repetitions: repetitions
    }
  end

end
