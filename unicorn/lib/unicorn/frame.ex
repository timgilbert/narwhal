defmodule Unicorn.Frame do
  alias Tint.RGB
  defstruct height: 16, width: 16, items: []

  @start_of_file 0x72

  @spec default_color :: RGB.t()
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

  @spec get(__MODULE__, integer, integer) :: RGB.t()
  def get(frame, x, y) do
    Enum.at(frame.items, x)
    |> Enum.at(y)
  end

  @spec hex_grid(__MODULE__) :: [[String.t()]]
  def hex_grid(frame) do
    Enum.map(frame.items, fn row ->
      Enum.map(row, &RGB.to_hex/1)
    end)
  end

  @spec color_to_binary(RGB.t()) :: binary
  def color_to_binary(color) do
    <<color.red, color.green, color.blue>>
  end

  @spec unicorn_binary(__MODULE__) :: binary
  def unicorn_binary(frame) do
    frame.items
    |> List.flatten()
    |> Enum.reduce(<<@start_of_file>>, fn c, acc -> <<acc <> color_to_binary(c)>> end)
  end

  @spec set(__MODULE__, non_neg_integer, non_neg_integer, RGB.t()) :: __MODULE__
  def set(frame, x, y, color) do
    row =
      Enum.at(frame.items, x)
      |> List.replace_at(y, color)

    %{
      frame
      | items: List.replace_at(frame.items, x, row)
    }
  end

  @spec grid(non_neg_integer, non_neg_integer, any) :: [[any]]
  def grid(w, h, f) do
    for _x <- 1..w do
      for _y <- 1..h, do: f.()
    end
  end

  @spec random_color :: RGB.t()
  def random_color() do
    <<r, g, b>> = :crypto.strong_rand_bytes(3)
    RGB.new(r, g, b)
  end
end
