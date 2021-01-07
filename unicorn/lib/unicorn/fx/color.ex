defmodule Unicorn.Fx.Color do
  alias Unicorn.Frame

  defmodule Struct do
    @type t :: %__MODULE__{color: Frame.color_t()}
    defstruct [:color]
  end

  @spec new(Frame.color_t) :: Struct.t()
  def new(color \\ Frame.default_color()) do
    %Unicorn.Fx.Color.Struct{color: color}
  end

  defimpl Unicorn.Fx.Effect, for: Unicorn.Fx.Color do
    @spec call(Struct.t(), Frame.t()) :: Frame.t()
    def call(effect, _f), do: Frame.new(color: effect.color)
  end
end
