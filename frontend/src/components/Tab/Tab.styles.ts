import styled from '@emotion/styled';
import { COLOR, Z_INDEX } from 'src/constants';

const Nav = styled.nav`
  max-width: 100%;
  min-width: 100%;

  height: 4rem;
  padding: 0.3rem;
  background-color: ${COLOR.WHITE_100};
  position: sticky;
  bottom: 0;
  left: 0;
  z-index: ${Z_INDEX.TAB_BAR};

  ul {
    display: flex;
    justify-content: space-between;
    width: 100%;

    li {
      width: 100%;

      a {
        display: block;
        width: 100%;
        height: 100%;
        text-align: center;

        svg {
          transition: transform 0.25s ease;
        }
      }

      a.active {
        color: ${COLOR.PURPLE_200};
        font-weight: 700;
        svg {
          transform: scale(1.3) translateY(-10%);
          fill: ${COLOR.PURPLE_200};
          stroke-width: 0.5rem;
          stroke: ${COLOR.WHITE_100};
        }
      }
    }
  }
`;

export { Nav };
