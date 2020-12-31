defmodule Unicorn do
  alias Circuits.SPI
  alias Unicorn.Frame
  import Enum

  # Port of https://github.com/pimoroni/unicorn-hat-hd/blob/master/library/unicornhathd/__init__.py
  # cf https://github.com/doceme/py-spidev

  @max_speed_hz 9_000_000
  @start_of_file 0x72

  @spec setup :: reference
  def setup do
    # def setup():
    # global _spi, _buf, is_setup

    # if is_setup:
    #     return

    # _spi = spidev.SpiDev()
    # _spi.open(0, 0)
    # _spi.max_speed_hz = 9000000

    # is_setup = True
    {:ok, pid} = SPI.open("spidev0.0", speed_hz: @max_speed_hz)
    pid
  end

  @spec upload(reference, Frame.t()) :: binary()
  def upload(pid, frame) do
    #     """Output the contents of the buffer to Unicorn HAT HD."""
    # setup()
    # _spi.xfer2([_SOF] + (numpy.rot90(_buf,_rotation).reshape(768) * _brightness).astype(numpy.uint8).tolist())
    # time.sleep(_DELAY)
    data = Frame.unicorn_binary(frame)
    {:ok, result} = SPI.transfer(pid, data)
    result
  end

  def rand_data() do
    Enum.reduce(1..(16 * 16 * 3), <<@start_of_file>>, fn _, acc ->
      <<acc::binary>> <> <<:rand.uniform(256) - 1::size(8)>>
    end)
  end

  def colorToBitString(c) do
    <<c.blue::size(8), c.green::size(8), c.blue::size(8)>>
  end

  def testBitString() do
    Enum.reduce([1, 2, 3], <<0x78>>, &appendByte/2)
  end

  defp appendByte(item, acc) do
    <<acc::binary>> <> <<item::size(8)>>
  end

  def matrixToBitString(matrix) do
    matrix
    |> Enum.reduce(fn a, b -> Enum.concat(b, a) end)
    |> Enum.map(&colorToBitString/1)
  end
end
