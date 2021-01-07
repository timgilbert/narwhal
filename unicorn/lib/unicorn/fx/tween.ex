defmodule Unicorn.Fx.Tween do
  alias Unicorn.Color
  alias Unicorn.Frame

  defmodule Struct do
    @type t :: %__MODULE__{target: Frame.t(), scale: float}
    defstruct target: nil, scale: 0.0
  end

  @spec new(Frame.t(), float) :: Struct.t()
  def new(target, scale) do
    %Struct{target: target, scale: scale}
  end

  defimpl Unicorn.Fx.Effect, for: Unicorn.Fx.Map do
    @spec call(Struct.t(), Frame.t()) :: Frame.t()
    def call(effect, frame) do
      Frame.map(frame, fn x, y, color ->
        target_color = Frame.get(effect.target, x, y)
        Color.color_tween(color, target_color, effect.scale)
      end)
    end
  end
end
