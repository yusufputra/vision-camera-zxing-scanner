type PointType = { x: number; y: number };
export default function calculateRotation(points: Array<PointType>): number {
  const a = points[1] as PointType;
  const b = points[2] as PointType;
  const z = Math.abs(a.x - b.x);
  const x = Math.abs(a.y - b.y);
  let angle = Math.round(Math.atan2(x, z) * (180 / Math.PI));

  // Quadrants 0 and 1
  if (a.y > b.y) {
    if (a.x > b.x) {
      angle = 90 + (90 - angle);
    }
  }
  // Quadrants 2 or 3
  else {
    if (a.x > b.x) {
      angle = 180 + angle;
    } else {
      angle = 360 - angle;
    }
  }
  return angle;
}
