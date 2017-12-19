defmodule UiWeb.PageView do
  use UiWeb, :view
  import ColorUtils
  require Logger

  def bounds() do
    1..16
  end

  def get_color(x, y) do
    rc = %ColorUtils.RGB{red: :rand.uniform(256) - 1, 
                         blue: :rand.uniform(256) - 1,
                         green: :rand.uniform(256) - 1}

    ColorUtils.rgb_to_hex(rc)
  end

  def grid() do
    for x <- 1..16 do
      for y <- 1..16 do
        x + y
      end
    end
  end
end
