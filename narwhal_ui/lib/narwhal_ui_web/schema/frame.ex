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
  object :frame_metadata do
    field :id, non_null(:string), description: "The unique ID of the saved frame"
    field :name, non_null(:string), description: "The display name of the frame"
    field :frame, non_null(:frame), description: "The frame data"
  end

  @desc "Response from a create frame mutation"
  object :create_frame_response do
    field :frame, non_null(:frame_metadata),
          description: "The frame that was just created"
    field :all_frames, non_null(list_of(non_null(:frame_metadata))),
          description: "The new list of all saved frames"
  end

  @desc "Input object for a new frame"
  input_object :new_frame_metadata do
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

  @desc "Input object for an updated frame. Note that we currently require the entire object."
  input_object :update_frame_request do
    field :id, non_null(:string),
          description: "The ID of the frame we're updating"
    field :name, non_null(:string),
          description: "If non-null, update the frame's name"
    field :frame, non_null(:new_frame_data),
          description: "If non-null, update the frame's data"
  end

  @desc "Input object for a frame deletion"
  input_object :delete_frame_request do
    field :id, non_null(:string), description: "The ID of the frame to delete"
  end

  @desc "Response from a delete frame mutation"
  object :delete_frame_response do
    field :frame_id, non_null(:string),
          description: "The ID of the frame that was just removed"
    field :all_frames, non_null(list_of(non_null(:frame_metadata))),
          description: "The new list of all saved frames"
  end

  @desc "Response from an update frame mutation"
  object :update_frame_response do
    field :frame, non_null(:frame_metadata),
          description: "The frame that was just updated"
    field :all_frames, non_null(list_of(non_null(:frame_metadata))),
          description: "The new list of all saved frames"
  end

end
