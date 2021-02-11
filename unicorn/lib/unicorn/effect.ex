defmodule Unicorn.Effect do
  alias Unicorn.Target

  defprotocol Effect do
    @spec immediate?(Effect.t()) :: Boolean
    def immediate?(effect)

    @spec pause_ms(Effect.t()) :: non_neg_integer
    def pause_ms(effect)

    @spec target(Effect.t()) :: FrameTarget.t()
    def target(effect)

    @spec granularity(Effect.t()) :: non_neg_integer
    def granularity(effect)

    @spec duration_ms(Effect.t()) :: non_neg_integer
    def duration_ms(effect)
  end

  defmodule Replace do
    @type t :: %__MODULE__{
                 target: FrameTarget,
                 pause_ms: non_neg_integer
               }
    defstruct [
      target: nil,
      pause_ms: 0
    ]

    @spec new(Target.FrameTarget.t(), non_neg_integer) :: t()
    def new(target, pause_ms) do
      %__MODULE__{target: target, pause_ms: pause_ms}
    end
  end

  defimpl Effect, for: Replace do
    def immediate?(_), do: true
    def granularity(_), do: 1
    def duration_ms(_), do: 0
    def pause_ms(effect), do: effect.pause_ms
    def target(effect), do: effect.target
  end

  defmodule Tween do
    @type t :: %__MODULE__{
                 pause_ms: non_neg_integer,
                 target: FrameTarget,
                 duration_ms: non_neg_integer,
                 granularity: pos_integer
               }
    defstruct [
      target: nil,
      pause_ms: 0,
      duration_ms: 0,
      granularity: 1
    ]
    @spec new(
            Target.FrameTarget.t(),
            non_neg_integer,
            non_neg_integer,
            pos_integer
          ) :: t()
    def new(
          target,
          pause_ms,
          duration_ms,
          granularity
        ) do
      %__MODULE__{
        target: target,
        pause_ms: pause_ms,
        duration_ms: duration_ms,
        granularity: granularity
      }
    end
  end

  defimpl Effect, for: Tween do
    def immediate?(_), do: false
    def duration_ms(effect), do: effect.duration_ms
    def granularity(effect), do: effect.granularity
    def pause_ms(effect), do: effect.pause_ms
    def target(effect), do: effect.target
  end

end
