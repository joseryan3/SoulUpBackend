import math
import urllib.request
from pathlib import Path

import cv2
import mediapipe as mp
from mediapipe.tasks import python as mp_python
from mediapipe.tasks.python import vision

from exercise_rules import MIN_LANDMARK_VISIBILITY

MODEL_URL = (
    "https://storage.googleapis.com/mediapipe-models/pose_landmarker/"
    "pose_landmarker_lite/float16/1/pose_landmarker_lite.task"
)
DEFAULT_MODEL_PATH = Path(__file__).resolve().parent / "models" / "pose_landmarker_lite.task"

LEFT_HIP = 23
RIGHT_HIP = 24
LEFT_KNEE = 25
RIGHT_KNEE = 26
LEFT_ANKLE = 27
RIGHT_ANKLE = 28


def _ensure_model(model_path: Path) -> Path:
    if model_path.exists() and model_path.stat().st_size > 0:
        return model_path

    model_path.parent.mkdir(parents=True, exist_ok=True)
    urllib.request.urlretrieve(MODEL_URL, str(model_path))
    return model_path


class PoseDetector:
    def __init__(self, model_path: Path | None = None):
        resolved_model = _ensure_model(model_path or DEFAULT_MODEL_PATH)

        base_options = mp_python.BaseOptions(model_asset_path=str(resolved_model))
        options = vision.PoseLandmarkerOptions(
            base_options=base_options,
            running_mode=vision.RunningMode.VIDEO,
            min_pose_detection_confidence=0.5,
            min_pose_presence_confidence=0.5,
            min_tracking_confidence=0.5,
        )
        self._landmarker = vision.PoseLandmarker.create_from_options(options)
        self._frame_index = 0

    def detect_knee_angle(self, frame):
        rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        mp_image = mp.Image(image_format=mp.ImageFormat.SRGB, data=rgb_frame)

        # Em modo VIDEO, o timestamp em ms deve ser estritamente crescente
        self._frame_index += 1
        timestamp_ms = self._frame_index * 33

        result = self._landmarker.detect_for_video(mp_image, timestamp_ms)

        if not result.pose_landmarks:
            return None

        landmarks = result.pose_landmarks[0]
        left_angle = self._calculate_side_angle(
            landmarks,
            LEFT_HIP,
            LEFT_KNEE,
            LEFT_ANKLE,
        )
        right_angle = self._calculate_side_angle(
            landmarks,
            RIGHT_HIP,
            RIGHT_KNEE,
            RIGHT_ANKLE,
        )

        angles = [angle for angle in (left_angle, right_angle) if angle is not None]
        if not angles:
            return None

        return sum(angles) / len(angles)

    def close(self):
        self._landmarker.close()

    def _calculate_side_angle(self, landmarks, hip_index, knee_index, ankle_index):
        hip = landmarks[hip_index]
        knee = landmarks[knee_index]
        ankle = landmarks[ankle_index]

        if not self._is_visible(hip, knee, ankle):
            return None

        return calculate_angle(
            (hip.x, hip.y),
            (knee.x, knee.y),
            (ankle.x, ankle.y),
        )

    @staticmethod
    def _is_visible(*points):
        return all(
            (getattr(point, "visibility", None) if getattr(point, "visibility", None) is not None else getattr(point, "presence", 1.0))
            >= MIN_LANDMARK_VISIBILITY
            for point in points
        )


def calculate_angle(point_a, point_b, point_c):
    radians = math.atan2(point_c[1] - point_b[1], point_c[0] - point_b[0]) - math.atan2(
        point_a[1] - point_b[1],
        point_a[0] - point_b[0],
    )
    angle = abs(math.degrees(radians))

    if angle > 180:
        angle = 360 - angle

    return angle