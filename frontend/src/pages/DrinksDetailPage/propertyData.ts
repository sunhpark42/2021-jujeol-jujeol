import React from 'react';
import HumanIcon from 'src/components/Icons/human';

interface Properties {
  content: string;
  Icon: ({ color }: { color: string }) => React.ReactElement;
}

// TODO: 어플리케이션이 시작될 때, 전체 categories를 불러오는 기능 필요
const CATEGORY_NAME = {
  0: '소주',
  1: '맥주',
  2: '막걸리',
};

const CATEGORY_ICON = {
  0: HumanIcon,
  1: HumanIcon,
  2: HumanIcon,
};

type categoryIdType = keyof typeof CATEGORY_NAME;

const getCategoryProperty = (categoryId: categoryIdType): Properties => ({
  content: CATEGORY_NAME[categoryId],
  Icon: CATEGORY_ICON[categoryId],
});

const getAbvIcon = (abv: number) => {
  if (abv <= 5) {
    return HumanIcon;
  }

  if (abv <= 20) {
    return HumanIcon;
  }

  if (abv <= 40) {
    return HumanIcon;
  }

  return HumanIcon;
};

const getAbvProperty = (abv: number) => ({
  content: `${abv}%`,
  Icon: getAbvIcon(abv),
});

const properties = [
  {
    id: 0,
    name: '주종',
    getProperty: ({ categoryId }: { categoryId: categoryIdType }) =>
      getCategoryProperty(categoryId),
  },
  {
    id: 1,
    name: '도수',
    getProperty: ({ alcoholByVolume }: { alcoholByVolume: number }) =>
      getAbvProperty(alcoholByVolume),
  },
];

export { properties };
export type { categoryIdType };
