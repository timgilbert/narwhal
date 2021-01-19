defmodule Unicorn.Frame do
  # TODO: replace with single-list implementation instead of nested list

  alias Unicorn.Color
  defstruct height: 16, width: 16, items: []
  @type t :: %__MODULE__{height: non_neg_integer, width: non_neg_integer, items: any}

  @start_of_file 0x72
  @spec default_color :: Color.t()
  def default_color, do: Color.black()

  @spec new(keyword()) :: t()
  def new(options \\ []) do
    %{height: height, width: width, color: color} =
      Enum.into(options, %{height: 16, width: 16, color: default_color()})

    shader =
      if Keyword.has_key?(options, :shader) do
        Keyword.get(options, :shader)
      else
        Color.constant_shader(color)
      end

    %__MODULE__{
      items: Color.create_grid(width, height, shader),
      height: height,
      width: width
    }
  end

  @spec rand(keyword) :: t()
  def rand(options \\ []) do
    new(Keyword.put(options, :shader, Color.random_shader()))
  end

  @spec get(t(), integer, integer) :: Color.t()
  def get(frame, x, y) do
    frame.items
    |> Enum.at(x)
    |> Enum.at(y)
  end

  @spec hex_grid(t()) :: [[String.t()]]
  def hex_grid(frame) do
    Enum.map(frame.items, fn row ->
      Enum.map(row, &Color.to_hex/1)
    end)
  end

  @spec pixels(t()) :: [Color.t()]
  def pixels(frame) do
    List.flatten(frame.items)
  end

  # TODO: this probably belongs in the hardware module
  @spec unicorn_binary(t()) :: binary
  def unicorn_binary(frame) do
    frame.items
    |> List.flatten()
    |> Enum.reduce(<<@start_of_file>>, fn c, acc -> <<acc <> Color.to_binary(c)>> end)
  end

  @spec set(t(), non_neg_integer, non_neg_integer, Color.t()) :: t()
  def set(frame, x, y, color) do
    row =
      Enum.at(frame.items, x)
      |> List.replace_at(y, color)

    %{
      frame
      | items: List.replace_at(frame.items, x, row)
    }
  end

  @spec map(t(), Color.color_fn()) :: t()
  def map(frame, pixel_fn) do
    items =
      for row <- frame.items do
        # TODO: count actual x/y values
        for item <- row, do: pixel_fn.(0, 0, item)
      end

    %{frame | items: items}
  end
end
