import styled from '@emotion/styled';
import Flex from 'src/styles/Flex';

const PreviewSection = styled.section`
  width: 100%;
  min-height: 10rem;
  padding: 1.3rem;
  padding-right: 0;
  margin: 0.5rem 0;

  div {
    ${Flex({ justifyContent: 'space-between', alignItems: 'start' })};

    h3 {
      font-size: 1.1rem;
      font-weight: 700;
      margin-bottom: 1rem;
    }
  }
`;

export { PreviewSection };
