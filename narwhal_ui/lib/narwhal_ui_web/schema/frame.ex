defmodule NarwhalUiWeb.Schema.Frame do
  @moduledoc false

  use Absinthe.Schema.Notation

  @desc "A single frame"
  object :frame do
    field :height, :integer, description: "Height of the frame in pixels"
    field :width, :integer, description: "Width of the frame in pixels"
    # TODO: custom scalar
    field :pixels, list_of(:rgb_color), description: "The pixels, as RGB hex strings" do
      resolve &NarwhalUiWeb.Resolvers.Frame.pixels/3
    end
  end

  @desc "Reponse from a create frame mutation"
  object :create_frame_response do
    field :frame, non_null(:frame), description: "The frame that was just created"
  end

  @desc "Input object for a new frame"
  input_object :new_frame do
    field :name, non_null(:string), description: "Name of the new frame"
    field :pixels, non_null(:rgb_color),
          description: "List of pixels, ordered horizontally from top right to bottom left"
  end

end
