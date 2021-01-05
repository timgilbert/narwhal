defmodule Unicorn.Fx.Color.Struct do
  @type t :: %__MODULE__{color: Frame.color_t()}
  defstruct [:color]
end

defmodule Unicorn.Fx.Color do
  alias Unicorn.Frame

  @spec new(Frame.color_t) :: Unicorn.Fx.Color.Struct.t()
  def new(color \\ Frame.default_color()) do
    %Unicorn.Fx.Color.Struct{color: color}
  end

  defimpl Unicorn.Fx.Effect, for: Unicorn.Fx.Color do
    @spec call(%Unicorn.Fx.Color.Struct{}, Frame.t()) :: Frame.t()
    def call(effect, _f), do: Frame.new(color: effect.color)
  end
end
