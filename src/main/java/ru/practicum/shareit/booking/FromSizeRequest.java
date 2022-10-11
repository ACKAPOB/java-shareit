    package ru.practicum.shareit.booking;

    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import ru.practicum.shareit.booking.exception.BadRequestException;

    import javax.validation.ValidationException;
    @RequiredArgsConstructor
    public class FromSizeRequest implements Pageable {

        private final int offset;
        private final int limit;
        private final Sort sort;

        public static Pageable of(Integer from, Integer size) throws ValidationException {
            if (from <= 0 && size <= 0)
                throw new BadRequestException("Ошибка, Pageable.FromSizeRequest()");
            return new FromSizeRequest(from, size, Sort.unsorted());
        }

        @Override
        public int getPageNumber() {
            return 0;
        }

        @Override
        public int getPageSize() {
            return limit;
        }

        @Override
        public long getOffset() {
            return offset;
        }

        @Override
        public Sort getSort() {
            return sort;
        }


        @Override
        public Pageable next() {
            return new FromSizeRequest(offset + limit, limit, sort);
        }

        @Override
        public Pageable previousOrFirst() {
            return new FromSizeRequest(offset, limit, sort);
        }

        @Override
        public Pageable first() {
            return new FromSizeRequest(offset, limit, sort);
        }

        @Override
        public Pageable withPage(int pageNumber) {
            return new FromSizeRequest(offset + limit * pageNumber, limit, sort);
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

    }
