defmodule Unicorn.Fx.Map do
  alias Unicorn.Color
  alias Unicorn.Frame

  defmodule Struct do
    @type t :: %__MODULE__{pixel_fn: Color.color_fn()}
    defstruct [:pixel_fn]
  end

  @spec new(Color.color_fn) :: Unicorn.Fx.Map.Struct.t()
  def new(pixel_fn) do
    %Unicorn.Fx.Map.Struct{pixel_fn: pixel_fn}
  end

  @spec lighten() :: Unicorn.Fx.Map.Struct.t()
  def lighten() do
    %Unicorn.Fx.Map.Struct{pixel_fn: &Color.lighten/1}
  end

  @spec darken() :: Unicorn.Fx.Map.Struct.t()
  def darken() do
    %Unicorn.Fx.Map.Struct{pixel_fn: &Color.darken/1}
  end

  defimpl Unicorn.Fx.Effect, for: Struct do
    @spec call(%Unicorn.Fx.Map.Struct{}, Frame.t()) :: Frame.t()
    def call(effect, frame) do
      Frame.map(frame, effect.pixel_fn)
    end
  end
end
