defmodule NarwhalUiWeb.Schema.Frame do
  @moduledoc false

  use Absinthe.Schema.Notation

  @desc "A single frame"
  object :frame do
    field :height, non_null(:integer), description: "Height of the frame in pixels"
    field :width, non_null(:integer), description: "Width of the frame in pixels"
    field :pixels, non_null(list_of(non_null(:rgb_color))) do
      description "The pixels, as RGB hex strings"
      resolve &NarwhalUiWeb.Resolvers.Frame.pixels/3
    end
  end

  @desc "A frame that has been saved with a name"
  object :named_frame do
    field :id, non_null(:string), description: "The unique ID of the saved frame"
    field :name, non_null(:string), description: "The display name of the frame"
    field :frame, non_null(:frame), description: "The frame data"
  end

  @desc "Response from a create frame mutation"
  object :save_frame_response do
    field :named_frame, non_null(:named_frame), description: "The frame that was just created"
  end

  @desc "Input object for a new frame"
  input_object :new_named_frame do
    field :name, non_null(:string), description: "Name of the new frame"
    field :frame, non_null(:new_frame_data) do
      description "The data for the frame we're saving"
    end
  end

  @desc "Input object for the frame data in a new frame"
  input_object :new_frame_data do
    field :height, :integer, description: "Height of the frame in pixels (default 16)"
    field :width, :integer, description: "Width of the frame in pixels (default 16)"
    field :pixels, non_null(list_of(non_null(:rgb_color))) do
      description "List of pixels, ordered horizontally from top right to bottom left"
    end
  end

  @desc "Input object for an updated frame"
  input_object :updated_frame do
    field :id, non_null(:string), description: "The unique ID of the frame to update"
    field :name, :string, description: "If non-null, update the frame's name"
    field :frame, :new_frame_data, description: "If set, update the frame data"
  end

end
