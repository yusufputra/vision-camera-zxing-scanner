const getAnswer = (angle: number) => {
  const MapAnwer: Record<number, string> = {
    4: 'A',
    3: 'B',
    2: 'C',
    1: 'D',
    0: 'A',
  };
  const answerKey = Math.round(angle / 90);
  const answer = MapAnwer[answerKey];
  return answer;
};

export default getAnswer;
