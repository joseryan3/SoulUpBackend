SQUAT_EXERCISE_NAME = "agachamento"

SQUAT_DOWN_ANGLE = 105
SQUAT_UP_ANGLE = 160
SQUAT_IDEAL_ANGLE = 90
MIN_LANDMARK_VISIBILITY = 0.55


def calculate_accuracy(rep_min_angles: list, analyzed_frames: int, frames_with_pose: int) -> float:
    """Pontuacao 0-100 do movimento (heuristica simples).

    60% profundidade: quanto mais perto de SQUAT_IDEAL_ANGLE o joelho chegou em cada repeticao
                      (perde 1/60 por grau acima do ideal).
    40% deteccao:     fracao dos frames em que o corpo foi detectado com clareza.
    Sem repeticoes completas ou sem frames, a pontuacao e 0.
    """
    if not rep_min_angles or analyzed_frames == 0:
        return 0.0

    depth_scores = [
        max(0.0, 1.0 - max(0.0, angle - SQUAT_IDEAL_ANGLE) / 60.0)
        for angle in rep_min_angles
    ]
    depth = sum(depth_scores) / len(depth_scores)
    pose_rate = frames_with_pose / analyzed_frames

    return round(100.0 * (0.6 * depth + 0.4 * pose_rate), 1)


def build_feedback(repetitions: int, analyzed_frames: int, frames_with_pose: int) -> str:
    if analyzed_frames == 0:
        return "Video vazio ou invalido."

    pose_rate = frames_with_pose / analyzed_frames

    if frames_with_pose == 0:
        return "Pose corporal nao detectada. Grave o corpo inteiro com boa iluminacao."

    if pose_rate < 0.5:
        return "Pose detectada em poucos frames. Ajuste camera, distancia e iluminacao."

    if repetitions == 0:
        return "Movimento insuficiente para contar repeticoes completas."

    return "Movimento consistente."
