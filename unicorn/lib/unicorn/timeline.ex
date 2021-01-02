defmodule Unicorn.Timeline do
  alias Unicorn.Step
  defstruct items: [], repeat?: false

  @spec new([%Step{}], boolean()) :: %__MODULE__{}
  def new(items, repeat?) do
    %__MODULE__{items: items, repeat?: repeat?}
  end
end
