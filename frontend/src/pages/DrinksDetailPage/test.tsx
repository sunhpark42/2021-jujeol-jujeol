import '@testing-library/jest-dom';
import { screen, render, waitFor, fireEvent } from '@testing-library/react';
import { MemoryRouter as Router } from 'react-router-dom';
import { LocationDescriptor } from 'history';
import APIProvider from 'src/apis/APIProvider';
import API from 'src/apis/requests';
import { drinksDetail } from 'src/mocks/drinksDetail';
import drinksReviews from 'src/mocks/drinksReviews';
import DrinksDetailPage from '.';
import { MockIntersectionObserver, mockScrollTo } from 'src/mocks/test';
import { UserProvider } from 'src/contexts/UserContext';

interface Props {
  initialEntries: LocationDescriptor[];
  children: React.ReactNode;
}

const customRender = ({ initialEntries, children }: Props) => {
  render(
    <APIProvider>
      <UserProvider>
        <Router initialEntries={initialEntries}>{children}</Router>
      </UserProvider>
    </APIProvider>
  );
};

describe('로그인 된 사용자가 상세페이지를 이용한다.', () => {
  beforeEach(async () => {
    Object.defineProperty(global.window, 'scrollTo', { value: mockScrollTo });
    Object.defineProperty(global.window, 'IntersectionObserver', {
      value: MockIntersectionObserver,
    });

    jest.spyOn(window, 'confirm').mockImplementation(() => {
      return true;
    });

    API.getUserInfo = jest.fn().mockReturnValue({ data: { id: 0, nickname: '123', bio: '1234' } });
    API.getDrink = jest.fn().mockReturnValue({ data: drinksDetail });
    API.getReview = jest
      .fn()
      .mockReturnValue({ data: drinksReviews.data, pageInfo: drinksReviews.pageInfo });

    customRender({ initialEntries: [`/drinks/0`], children: <DrinksDetailPage /> });

    await waitFor(() => expect(API.getUserInfo).toBeCalledTimes(1));
    await waitFor(() => expect(API.getDrink).toBeCalledTimes(1));
    await waitFor(() => expect(API.getReview).toBeCalledTimes(1));
  });

  it('사용자는 상세페이지에서 주류 정보를 확인할 수 있다.', async () => {
    expect(screen.getByAltText(drinksDetail.name).getAttribute('src')).toBe(drinksDetail.imageUrl);
    expect(screen.getByText(drinksDetail.name)).toBeVisible();
    expect(
      screen.getByText(`(${drinksDetail.englishName}, ${drinksDetail.alcoholByVolume}%)`)
    ).toBeVisible();
    expect(screen.getByText(`당신의 선호도는? ${drinksDetail.preferenceRate} 점`)).toBeVisible();
    expect(
      screen.getByText(`다른 사람들은 평균적으로 ${drinksDetail.preferenceAvg}점을 줬어요`)
    ).toBeVisible();
  });

  it('로그인 된 사용자는 상세페이지에서 선호도를 남길 수 있다.', async () => {
    const preferenceRate = 4.5;
    const preferenceInput = screen.getByRole('slider');

    fireEvent.click(preferenceInput);
    expect(window.confirm).not.toBeCalled();

    fireEvent.change(preferenceInput, { target: { value: preferenceRate } });
    expect(screen.getByText(`당신의 선호도는? ${preferenceRate} 점`)).toBeVisible();
  });

  it('로그인 된 사용자는 상세페이지에서 선호도를 삭제할 수 있다.', async () => {
    const preferenceRate = 0;
    const preferenceInput = await screen.findByRole('slider');

    fireEvent.click(preferenceInput);
    expect(window.confirm).not.toBeCalled();

    fireEvent.change(preferenceInput, { target: { value: preferenceRate } });
    expect(screen.getByText(`선호도를 입력해주세요`)).toBeVisible();
  });
});

describe('로그인 되지 않은 사용자가 상세페이지를 이용한다.', () => {
  beforeEach(async () => {
    Object.defineProperty(global.window, 'scrollTo', { value: mockScrollTo });
    Object.defineProperty(global.window, 'IntersectionObserver', {
      value: MockIntersectionObserver,
    });

    jest.spyOn(window, 'confirm').mockImplementation(() => {
      return true;
    });

    API.getUserInfo = jest.fn().mockImplementation(() => {
      throw new Error();
    });
    API.getDrink = jest.fn().mockReturnValue({ data: drinksDetail });
    API.getReview = jest
      .fn()
      .mockReturnValue({ data: drinksReviews.data, pageInfo: drinksReviews.pageInfo });

    customRender({ initialEntries: [`/drinks/0`], children: <DrinksDetailPage /> });

    await waitFor(() => expect(API.getUserInfo).toBeCalledTimes(1));
    await waitFor(() => expect(API.getDrink).toBeCalledTimes(1));
    await waitFor(() => expect(API.getReview).toBeCalledTimes(1));
  });

  it('로그인 되지 않은 사용자가 상세페이지에서 선호도를 남기려고 할 때 로그인하라는 창이 뜬다.', async () => {
    const preferenceInput = await screen.findByRole('slider');

    fireEvent.click(preferenceInput);
    expect(window.confirm).toBeCalled();
  });
});
