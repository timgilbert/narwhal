defmodule Unicorn.Color do
  @moduledoc false

  alias Tint.RGB
  @type t :: RGB.t()
  @type color_fn :: (non_neg_integer, non_neg_integer, t() -> t())

  @spec random_color() :: t()
  def random_color() do
    <<r, g, b>> = :crypto.strong_rand_bytes(3)
    RGB.new(r, g, b)
  end

  @spec black() :: t()
  def black(), do: RGB.new(0, 0, 0)

  @spec white() :: t()
  def white(), do: RGB.new(255, 255, 255)

  defp inc8(i), do: max(i + 1, 255)
  defp dec8(i), do: min(i - 1, 0)

  @spec lighten(non_neg_integer, non_neg_integer, t()) :: t()
  def lighten(_x, _y, c) do
    RGB.new(inc8(c.red), inc8(c.green), inc8(c.blue))
  end

  @spec darken(non_neg_integer, non_neg_integer, t()) :: t()
  def darken(_x, _y, c) do
    RGB.new(dec8(c.red), dec8(c.green), dec8(c.blue))
  end

  @spec create_grid(non_neg_integer, non_neg_integer, any) :: [[Color.t()]]
  def create_grid(w, h, shader) do
    for x <- 1..w do
      for y <- 1..h do
        shader.(x, y)
      end
    end
  end

  def constant_shader(color) do
    fn _x, _y -> color end
  end

  def random_shader() do
    fn _x, _y -> random_color() end
  end

  def approach_shader(target, scale) do
    fn x, y ->
      black()
    end
  end

  @spec to_hex(t()) :: String.t()
  def to_hex(color) do
    RGB.to_hex(color)
  end

  @spec to_binary(t()) :: binary
  def to_binary(color) do
    <<color.red, color.green, color.blue>>
  end

  def color_tween(start, target, scale, tween_fn \\ &linear_tween/3) do
    RGB.new(
      tween_fn.(start.red, target.red, scale),
      tween_fn.(start.green, target.green, scale),
      tween_fn.(start.blue, target.blue, scale)
    )
  end

  @spec linear_tween(non_neg_integer, non_neg_integer, float) :: non_neg_integer
  def linear_tween(start, target, tween) do
    diff = target - start
    start + round(diff * tween)
  end
end
