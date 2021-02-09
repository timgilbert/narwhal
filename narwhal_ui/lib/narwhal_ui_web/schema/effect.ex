defmodule NarwhalUiWeb.Schema.Effect do
  @moduledoc false

  use Absinthe.Schema.Notation
  alias NarwhalUiWeb.Resolvers.Effect

  @desc "The type of an effect"
  enum :effect_type do
    value :replace, description: "Replace the frame with a target frame"
    value :tween, description: "Gradually nudge the frame towards the given frame"
    value :darken, description: "Gradually darken the frame"
    value :lighten, description: "Gradually lighten the frame"
  end

  @desc "For replace and tween effects, the type of target frame"
  enum :frame_target_type do
    value :random_frame,
          description: "A randomly-generated frame"
    value :solid_frame,
          description: "A frame which has been set to a specific color"
    value :saved_frame,
          description: "A saved frame"
  end

  @desc "A single effect on a timeline"
  object :effect do
    field :type, non_null(:effect_type) do
      description "The type of effect this object represents."
      resolve &Effect.effect_type/3
    end
    field :pause_ms, :integer do
      description """
      Number of milliseconds to pause after the event has
      finished.
      """
    end
    field :duration_ms, :integer do
      description """
      Total duration of this effect. Set to 0 in the case of
      replacement effects. The duration is the amount of time
      spent performing the effect. Once the effect is finished,
      the `pauseMs` field can be used to pause on the final
      frame.
      """
    end
    field :granularity, :integer do
      description """
      For effects that have a duration, the number of discrete steps
      to perform during that duration. For example, if an effect's
      duration is 1000ms and its granularity is set to 10, the effect
      will generate 10 intermediate frames of 100ms each.

      For immediate effects, the granularity is always 1.
      """
    end
    # TODO: lighten/darken probably don't need this. Maybe there should
    # be a "percentage" field for those? tween 50% of the way to a new frame?
    field :target, non_null(:frame_target) do
      description """
      The target frame for an effect. For replacement effects, the target
      is immediately generated and returned. For continuous ones such
      as tween, a series of intermediate frames are generated.
      """
    end
  end

  @desc "A single effect on a timeline"
  interface :frame_target do
    # TODO: just use __typename?
    field :type, non_null(:frame_target_type),
          description: "The type of this frame target."
    resolve_type fn
      %Unicorn.Fx.Random.Struct{}, _ -> :random_effect
      %Unicorn.Fx.Color.Struct{}, _ -> :color_effect
      _, _ -> nil # TODO: log error
    end
  end

  @desc "Frame target representing a randomly-generated grid"
  object :random_frame_target do
    interfaces [:frame_target]
    field :type, non_null(:frame_target_type) do
      resolve fn _, _ -> :random_frame end
    end
  end

  @desc "Frame target representing a solid, uniform color"
  object :solid_frame_target do
    interface :frame_target
    field :type, non_null(:frame_target_type) do
      resolve fn _, _ -> :solid_frame end
    end
    field :color, :string, description: "The color of the target frame"
  end

  # TODO: this sort of requires us to pull the frame repo into this library
  # Maybe we could provide the lib with a get-frame-by-id resolver to
  # keep it storage-independent?
  @desc "Frame target representing a solid, uniform color"
  object :saved_frame_target do
    interface :frame_target
    field :type, non_null(:frame_target_type) do
      resolve fn _, _ -> :saved_frame end
    end
    field :id, :string, description: "The unique ID of the saved frame"
  end

  # Effect and frame target inputs
  @desc "The target of an effect"
  input_object :frame_target_input do
    field :type, non_null(:frame_target_type) do
      description "The type of this frame target"
    end
    field :color, :string do
      description """
      For `solidFrameTarget` objects, the color to set the frame
      to. Ignored for other types of frame target.
      """
    end
    field :frame_id, :string do
      description """
      For savedFrameTarget objects, the ID of this frame. Ignored
      for other types of frame targets.
      """
    end
  end

  @desc "Input object for a single effect"
  input_object :effect_input do
    field :type, non_null(:effect_type),
          description: "The type of effect this object represents."
    field :pause_ms, :integer do
      description """
      Number of milliseconds to pause after the event has finished.
      Defaults to 0.
      """
    end
    field :duration_ms, :integer do
      description """
      Total duration of this effect. Set to 0 in the case of
      replacement effects. The duration is the amount of time
      spent performing the effect. Once the effect is finished,
      the `pauseMs` field can be used to pause on the final
      frame.
      """
    end
    field :granularity, :integer do
      description """
      For effects that have a duration, the number of discrete steps
      to perform during that duration. For example, if an effect's
      duration is 1000ms and its granularity is set to 10, the effect
      will generate 10 intermediate frames of 100ms each.

      For immediate effects, the granularity is always 1.
      """
    end
    field :target, :frame_target_input do
      description """
      The target frame for effects which require one (replace, tween).
      """
    end

  end
end
