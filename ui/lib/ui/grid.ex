defmodule Ui.Grid do
  use GenServer
  import ColorUtils

  @height 16
  @width 16

  # Client API
  def start_link() do
    GenServer.start_link(__MODULE__, :ok, name: UiGrid)
    # GenServer.start_link(__MODULE__, :ok, opts)
  end

  def clear(server) do
    GenServer.call(server, {:clear})
  end

  def randomize(server) do
    GenServer.call(server, {:randomize})
  end

  def paint(server, x, y, color) do
    GenServer.call(server, {:paint, x, y, color})
  end

  def get_hex(server, x, y) do
    GenServer.call(server, {:get_hex, x, y})
  end

  # Server API
  def init(:ok) do
    {:ok, blank_grid()}
  end

  def handle_call({:clear}, _from, state) do
    {:reply, :initialized, blank_grid()}
  end

  def handle_call({:randomize}, _from, state) do
    {:reply, :initialized, random_grid()}
  end

  def handle_call({:get_hex, x, y}, _from, state) do
    {:reply, coords_hex(state, x, y), state}
  end

  # Implementation

  defp coords_hex(state, x, y) do
    ColorUtils.rgb_to_hex(get(state, x, y))
  end

  defp get(state, x, y) do
    Enum.fetch!(Enum.fetch!(state, x), y)
  end

  defp init_grid(cell_fn) do
    for _x <- 1..@height do
      for _y <- 1..@width do
        cell_fn.()
      end
    end
  end

  defp blank_grid() do
    init_grid(&black_color/0)
  end

  defp random_grid() do
    init_grid(&random_color/0)
  end

  defp random_color() do
    %ColorUtils.RGB{red: :rand.uniform(256) - 1,
                    green: :rand.uniform(256) - 1,
                    blue: :rand.uniform(256) - 1}
  end

  defp black_color() do
    %ColorUtils.RGB{red: 0, green: 0, blue: 0}
  end

end
