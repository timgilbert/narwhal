defmodule Unicorn.Frame do
  alias Tint.RGB
  defstruct height: 16, width: 16, items: []
  @type t :: %__MODULE__{height: non_neg_integer, width: non_neg_integer, items: any}

  @start_of_file 0x72
  @default_color RGB.new(0, 0, 0)

  @spec new(keyword()) :: t()
  def new(options \\ []) do
    %{height: height, width: width, color: color} =
      Enum.into(options, %{height: 16, width: 16, color: @default_color})

    shader =
      if Keyword.has_key?(options, :shader) do
        Keyword.get(options, :shader)
      else
        constant_shader(color)
      end

    %__MODULE__{
      items: create_grid(width, height, shader),
      height: height,
      width: width
    }
  end

  @spec rand(keyword) :: t()
  def rand(options \\ []) do
    new(Keyword.put(options, :shader, random_shader()))
  end

  @spec get(t(), integer, integer) :: RGB.t()
  def get(frame, x, y) do
    Enum.at(frame.items, x)
    |> Enum.at(y)
  end

  @spec hex_grid(t()) :: [[String.t()]]
  def hex_grid(frame) do
    Enum.map(frame.items, fn row ->
      Enum.map(row, &RGB.to_hex/1)
    end)
  end

  @spec color_to_binary(RGB.t()) :: binary
  def color_to_binary(color) do
    <<color.red, color.green, color.blue>>
  end

  @spec unicorn_binary(t()) :: binary
  def unicorn_binary(frame) do
    frame.items
    |> List.flatten()
    |> Enum.reduce(<<@start_of_file>>, fn c, acc -> <<acc <> color_to_binary(c)>> end)
  end

  @spec set(t(), non_neg_integer, non_neg_integer, RGB.t()) :: t()
  def set(frame, x, y, color) do
    row =
      Enum.at(frame.items, x)
      |> List.replace_at(y, color)

    %{
      frame
      | items: List.replace_at(frame.items, x, row)
    }
  end

  @spec create_grid(non_neg_integer, non_neg_integer, any) :: [[RGB.t()]]
  defp create_grid(w, h, shader) do
    for x <- 1..w do
      for y <- 1..h, do: shader.(x, y)
    end
  end

  defp constant_shader(color) do
    fn _x, _y -> color end
  end

  defp random_shader() do
    fn _x, _y -> random_color() end
  end

  @spec random_color :: RGB.t()
  def random_color() do
    <<r, g, b>> = :crypto.strong_rand_bytes(3)
    RGB.new(r, g, b)
  end
end
