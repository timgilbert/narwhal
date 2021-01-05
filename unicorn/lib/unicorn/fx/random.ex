defmodule Unicorn.Fx.Random do
  alias Unicorn.Frame

  defmodule Struct do
    @type t :: %__MODULE__{}
    defstruct [:random]
  end

  @spec new() :: Unicorn.Fx.Random.Struct.t()
  def new() do
    %Unicorn.Fx.Color.Struct{}
  end

  defimpl Unicorn.Fx.Effect, for: Random do
    @spec call(any, Frame.t()) :: Frame.t()
    def call(_c, _f), do: Frame.rand()
  end
end
