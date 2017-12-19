defmodule UiWeb.PageView do
  use UiWeb, :view
  import ColorUtils
  import Ui.Grid
  require Logger

  def bounds() do
    1..16
  end

  def get_color(x, y) do
    # hmm
    # Ui.Grid.get_hex(UiGrid, x, y)
    rc = %ColorUtils.RGB{red: :rand.uniform(256) - 1,
                         blue: :rand.uniform(256) - 1,
                         green: :rand.uniform(256) - 1}

    ColorUtils.rgb_to_hex(rc)
  end

end
