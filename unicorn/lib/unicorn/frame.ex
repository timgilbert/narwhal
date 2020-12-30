defmodule Unicorn.Frame do
  alias Tint.RGB
  defstruct ~w[height width items]a

  @spec default_color :: Tint.RGB.t()
  def default_color() do
    RGB.new(0, 0, 0)
  end

  @defaults %{height: 16, width: 16, color: &Unicorn.Frame.default_color/0}

  def new(options \\ []) do
    %{height: height, width: width, color: color} = Enum.into(options, @defaults)

    %__MODULE__{
      items: grid(width, height, color),
      height: height,
      width: width
    }
  end

  def get(frame, x, y) do
    Enum.at(frame.items, x)
    |> Enum.at(y)
  end

  defp grid(w, h, f) do
    for _x <- 0..w do
      for _y <- 0..h, do: f.()
    end
  end

  def random_color() do
    <<r, g, b>> = :crypto.strong_rand_bytes(3)
    RGB.new(r, g, b)
  end
end
